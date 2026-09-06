package com.kh.khedu.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AttemptAnswerDao;
import com.kh.khedu.dao.AttemptDao;
import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.AttemptAnswerDto;
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.AlreadyExistsException;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.exam.StudentExamDetailVO;

@Service
@Transactional
public class AttemptServiceImpl implements AttemptService {

    @Autowired
    private AttemptDao attemptDao;
    
    @Autowired
    private ExamDao examDao;
    
    @Autowired
    private AttemptAnswerDao attemptAnswerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionOptionDao questionOptionDao;
    
    // 시험 응시 시작
    @Override
    public int insert(AttemptDto attemptDto) {
    	int examNo = attemptDto.getExamNo();
    	int studentNo = attemptDto.getStudentNo();
    	
    	//학생이 접근 가능한 시험인지 확인
    	StudentExamDetailVO exam = 
    			examDao.selectDetailByStudent(examNo, studentNo);
    	
    	if(exam == null) {
    		throw new TargetNotfoundException();
    	}
    	
    	//공개된 시험인지 확인
    	if(!"공개".equals(exam.getExamStatus())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"응시할 수 없는 시험입니다."
    		);
    	}
    	
    	Timestamp now = new Timestamp(System.currentTimeMillis());

    	//시험 시작 전
    	if (now.before(exam.getExamStart())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"아직 시험 응시 시간이 아닙니다."
    		);
    	}
    	
    	//시험 종료
    	if(!now.before(exam.getExamEnd())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"시험 응시 시간이 종료되었습니다."
    		);
    	}
    	
    	//이미 응시한 시험인지 확인
    	AttemptDto findDto = attemptDao.selectOneByExamStudent(examNo, studentNo);

    	if(findDto != null) {
    		throw new AlreadyExistsException();
    	}
        // 응시 번호 생성
        int attemptNo = attemptDao.sequence();
        attemptDto.setAttemptNo(attemptNo);

        // 최초 응시 상태
        attemptDto.setAttemptStatus("응시중");
        // 응시 등록
        attemptDao.insert(attemptDto);

        return attemptNo;
    }

    // 응시 번호로 단일 응시 조회
    @Override
    public AttemptDto selectOne(int attemptNo) {

        AttemptDto attempt = attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        return attempt;
    }

    // 특정 학생의 특정 시험 응시 조회
    @Override
    public AttemptDto selectOneByExamStudent(
            int examNo,
            int studentNo) {

        return attemptDao.selectOneByExamStudent(
                examNo,
                studentNo
        );
    }

    // 특정 시험의 전체 응시 목록 조회
    @Override
    public List<AttemptDto> selectListByExam(int examNo) {

        return attemptDao.selectListByExam(examNo);
    }

    // 특정 학생의 전체 응시 목록 조회
    @Override
    public List<AttemptDto> selectListByStudent(int studentNo) {

        return attemptDao.selectListByStudent(studentNo);
    }

    // 시험 제출
    @Override
    public boolean submit(int attemptNo, int studentNo) {
        // 응시정보 확인
        AttemptDto attempt = attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        // 본인 시험인지
        if (attempt.getStudentNo() != studentNo) {
            throw new GetOutException();
        }

        // 이미 제출한 시험
        if ("제출완료".equals(attempt.getAttemptStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 제출한 시험입니다."
            );
        }

        // 해당 시험의 전체 문제
        List<QuestionDto> questionList = questionDao.selectListByExam(attempt.getExamNo());

        // 학생이 저장한 전체 답안
        List<AttemptAnswerDto> answerList = attemptAnswerDao.selectListByAttempt(attemptNo);

        int totalScore = 0;

        for (QuestionDto question : questionList) {
            // 현재 문제의 학생 답안 찾기
            AttemptAnswerDto answer =
                    answerList.stream()
                            .filter(a -> a.getQuestionNo() == question.getQuestionNo())
                            .findFirst()
                            .orElse(null);

            // 답을 안 한 문제
            if (answer == null || answer.getOptionNo() == null) {
                continue;
            }

            // 학생이 선택한 보기 조회
            QuestionOptionDto option = questionOptionDao.selectOne(answer.getOptionNo());

            if (option == null) {
                throw new TargetNotfoundException();
            }

            // 혹시라도 잘못된 데이터가 저장되어 있는지 재검증
            if (option.getQuestionNo() != question.getQuestionNo()) {
                throw new GetOutException();
            }


            // 서버에서 정답 여부 계산
            String isCorrect = "Y".equals(option.getOptionIsAnswer()) ? "Y": "N";


            // 채점 결과 저장
            attemptAnswerDao.updateCorrect(attemptNo, question.getQuestionNo(), isCorrect);

            // 정답이면 문제 점수 합산
            if ("Y".equals(isCorrect)) {totalScore += question.getQuestionScore();}
        }


        // 서버에서 계산한 최종 점수
        AttemptDto submitDto =
        		AttemptDto.builder()
                    .attemptNo(attemptNo)
                    .attemptScore(totalScore)
                .build();
        
        // 제출완료 + 제출시간 + 점수 저장
        return attemptDao.submit(submitDto);
    }

    // 응시 상태 수정
    @Override
    public boolean updateStatus(AttemptDto attemptDto) {

        // 응시 존재 확인
        AttemptDto findAttempt =attemptDao.selectOne(attemptDto.getAttemptNo());

        if (findAttempt == null) {
            throw new TargetNotfoundException();
        }

        return attemptDao.updateStatus(attemptDto);
    }

    // 응시 삭제
    @Override
    public boolean delete(int attemptNo) {

        // 응시 존재 확인
        AttemptDto attempt =
                attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        return attemptDao.delete(attemptNo);
    }
    
    //제한시간 검사 및 응시 가능 시간 검사
    @Override
    public AttemptDto checkAvailableAttempt(
            int attemptNo,
            int studentNo) {

        // 응시 조회
        AttemptDto attempt = attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }
        
        // 본인 응시인지
        if (attempt.getStudentNo() != studentNo) {
            throw new GetOutException();
        }

        // 이미 제출한 시험
        if ("제출완료".equals(attempt.getAttemptStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 제출한 시험입니다."
            );
        }

        // 시험 조회
        ExamDto exam = examDao.selectOne(attempt.getExamNo());

        if (exam == null) {
            throw new TargetNotfoundException();
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime examStart = exam.getExamStart().toLocalDateTime();

        LocalDateTime examEnd = exam.getExamEnd().toLocalDateTime();

        // 전체 시험 시작 전
        if (now.isBefore(examStart)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "아직 시험 응시 시간이 아닙니다."
            );
        }

        // 개인 제한시간을 적용하기 전 기본 종료시간
        LocalDateTime deadline = examEnd;

        // examLimit이 존재하는 경우
        if (exam.getExamLimit() != null) {
            LocalDateTime personalDeadline =
                    attempt.getAttemptStart()
                            .toLocalDateTime()
                            .plusMinutes(exam.getExamLimit());

            // 전체 시험 종료시간과 개인 종료시간 중
            // 더 빠른 시간을 사용
            if (personalDeadline.isBefore(deadline)) {
                deadline = personalDeadline;
            }
        }


        // 시험 종료
        if (!now.isBefore(deadline)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "시험 응시 시간이 종료되었습니다."
            );
        }

        return attempt;
    }
}