package com.kh.khedu.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dao.AttemptAnswerDao;
import com.kh.khedu.dao.AttemptDao;
import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.dto.AttemptAnswerDto;
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.AlreadyExistsException;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamResultVO;
import com.kh.khedu.vo.exam.QuestionResultOptionVO;
import com.kh.khedu.vo.exam.QuestionResultVO;
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
    
    @Autowired
    private AttachDao attachDao;
    
    //공통메서드
    private AttemptDto checkSubmittableAttempt(
    		int attemptNo,
    		int studentNo) {
    	AttemptDto attempt = attemptDao.selectOne(attemptNo);
    	
    	if(attempt == null) {
    		throw new TargetNotfoundException();
    	}
    	
    	//본인 응시인지
    	if(attempt.getStudentNo() != studentNo) {
    		throw new GetOutException();
    	}
    	
    	//이미 제출 완료 
    	if("제출완료".equals(attempt.getAttemptStatus())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"이미 제출한 시험입니다.");
    	}
    	
    	ExamDto exam = examDao.selectOne(attempt.getExamNo());
    	
    	if(exam == null) {
    		throw new TargetNotfoundException();
    	}
    	
    	//시험 시작 전에 제출하는 것을 막음
    	long now = System.currentTimeMillis();
    	
    	if(now < exam.getExamStart().getTime()) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"아직 시험 응시 시간이 아닙니다.");
    	}
    	return attempt;
    	
    }
    
    // 제출 완료된 응시인지 확인
    private AttemptDto checkSubmittedAttempt(int attemptNo) {
        AttemptDto attempt = attemptDao.selectOne(attemptNo);
        
        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        if (!"제출완료".equals(attempt.getAttemptStatus()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "제출 완료된 시험만 결과를 확인할 수 있습니다."
            );
        }

        return attempt;
    }
    
 // 시험 결과 VO 조립
    private ExamResultVO buildResult(AttemptDto attempt) {
        // =========================
        // 시험 정보 조회
        // =========================
        ExamDetailVO exam = examDao.selectDetail(attempt.getExamNo());

        if (exam == null) {
            throw new TargetNotfoundException();
        }

        // =========================
        // 학생 이름 조회
        // =========================

        String studentName = attemptDao.selectStudentName(attempt.getStudentNo());

        // =========================
        // 문제 / 답안 조회
        // =========================

        List<QuestionDto> questionList = questionDao.selectListByExam(attempt.getExamNo());

        List<AttemptAnswerDto> answerList =attemptAnswerDao.selectListByAttempt(attempt.getAttemptNo());


        List<QuestionResultVO> resultQuestionList = new ArrayList<>();

        int totalScore = 0;

        // =========================
        // 문제별 결과 조립
        // =========================

        for (QuestionDto question : questionList) {
            // 시험 총점 계산
            totalScore += question.getQuestionScore();

            // 현재 문제에 대한 학생 답안 조회
            AttemptAnswerDto answer =
                    answerList.stream()
                            .filter(item ->
                                    item.getQuestionNo()
                                            == question.getQuestionNo()
                            )
                            .findFirst()
                            .orElse(null);

            // =========================
            // 보기 조회
            // =========================

            List<QuestionOptionDto> optionList = questionOptionDao.selectListByQuestion(question.getQuestionNo());

            List<QuestionResultOptionVO> resultOptionList =
                    optionList.stream()
                            .map(option -> {
                                // 학생이 선택한 보기인지
                                boolean selected =
                                        answer != null
                                        && answer.getOptionNo() != null
                                        && answer.getOptionNo()
                                        == option.getOptionNo();
                                // 실제 정답 보기인지
                                boolean correct =
                                        "Y".equals(
                                                option.getOptionIsAnswer()
                                        );
                                
                                return QuestionResultOptionVO.builder()
                                        .optionNo(option.getOptionNo())
                                        .optionContent(option.getOptionContent())
                                        .optionOrder(option.getOptionOrder())
                                        .selected(selected)
                                        .correct(correct)
                                        .build();
                            })
                            .toList();

            // =========================
            // 첨부파일 조회
            // =========================

            List<Integer> fileNos = questionDao.selectFiles(question.getQuestionNo());

            List<AttachDto> fileList;

            if (fileNos == null || fileNos.isEmpty()) {
                fileList = List.of();
            }
            else {
                fileList = attachDao.selectList(fileNos);
            }

            // =========================
            // 문제 결과 VO 조립
            // =========================
            QuestionResultVO questionResult =
                    QuestionResultVO.builder()
                            .questionNo(question.getQuestionNo())
                            .questionContent(question.getQuestionContent())
                            .questionScore(question.getQuestionScore())
                            .questionOrder(question.getQuestionOrder())
                            .questionComment(question.getQuestionComment())
                            // 미응답이면 null
                            .isCorrect(answer == null ? null : answer.getIsCorrect())
                            .optionList(resultOptionList)
                            .fileList(fileList)
                            .build();

            resultQuestionList.add(questionResult);
        }

        // =========================
        // 시험 전체 결과 VO 조립
        // =========================
        return ExamResultVO.builder()
                .attemptNo(attempt.getAttemptNo())
                .examNo(exam.getExamNo())
                // 학생 정보
                .studentNo(attempt.getStudentNo())
                .studentName(studentName)
                // 강의 / 시험 정보
                .courseNo(exam.getCourseNo())
                .courseTitle(exam.getCourseTitle())
                .examTitle(exam.getExamTitle())
                // 점수
                .attemptScore(attempt.getAttemptScore() == null ? 0 : attempt.getAttemptScore())
                .totalScore(totalScore)
                // 제출시간
                .attemptSubmit(attempt.getAttemptSubmit())
                // 문제별 결과
                .questionList(resultQuestionList)
                .build();
    }
    
    
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

    // 시험 제출
    @Override
    public boolean submit(int attemptNo, int studentNo) {
        // 본인 확인 + 재제출 방지
    	// 시간 초과 상태여도 제출은 허용
        AttemptDto attempt = checkSubmittableAttempt(attemptNo, studentNo);

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

 // 학생 시험 결과 조회
    @Override
    public ExamResultVO selectResult(
            int attemptNo,
            int studentNo) {

        // 응시 존재 여부 + 제출완료 여부 확인
        AttemptDto attempt = checkSubmittedAttempt(attemptNo);

        // 본인의 응시인지 확인
        if (attempt.getStudentNo() != studentNo) {
            throw new GetOutException();
        }
        
        ExamDto exam = examDao.selectOne(attempt.getExamNo());
        
        if (exam == null) {
            throw new TargetNotfoundException();
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examEnd = exam.getExamEnd().toLocalDateTime();
        
        if (now.isBefore(examEnd)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "시험 종료 후 결과를 확인할 수 있습니다."
            );
        }
        
        // 결과 VO 조립
        return buildResult(attempt);
    }

	@Override
	public ExamResultVO selectResultByManage(int attemptNo, int employeeNo, boolean tutor) {
		// 응시 존재 여부 + 제출완료 여부 확인
        AttemptDto attempt = checkSubmittedAttempt(attemptNo);
        
        //응시한 시험 조회
        ExamDto exam = examDao.selectOne(attempt.getExamNo());
        
        if(exam == null) {
        	throw new TargetNotfoundException();
        }
        
        //강사라면 본인이 출제한 시험인지 확인
        if(tutor && exam.getEmployeeNo() != employeeNo) {
        	throw new GetOutException();
        }
        
        //학생용과 동일한 결과VO 조립
        return buildResult(attempt);
	}
}