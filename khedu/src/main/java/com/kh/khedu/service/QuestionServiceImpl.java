package com.kh.khedu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dao.AttemptDao;
import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.exam.StudentQuestionOptionVO;
import com.kh.khedu.vo.exam.StudentQuestionVO;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AttachService attachService;
    
    @Autowired
    private AttemptDao attemptDao;
    
    @Autowired
    private QuestionOptionDao questionOptionDao;
    
    @Autowired
    private AttachDao attachDao;
    
    @Autowired
    private ExamDao examDao;
    
    //공통 메서드
    private ExamDto checkEditableExam(int examNo, int employeeNo, boolean tutor) {
        ExamDto exam = examDao.selectOne(examNo);

        if (exam == null) {
            throw new TargetNotfoundException();
        }

        // 강사는 본인 시험만 관리 가능
        if (tutor && exam.getEmployeeNo() != employeeNo) {
            throw new GetOutException();
        }

        // 문제/보기 편집은 작성중일 때만 가능
        if (!"작성중".equals(exam.getExamStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "공개된 시험의 문제는 수정할 수 없습니다."
            );
        }

        return exam;
    }
    
    //문제 순서 확인
    private void checkQuestionOrderDuplicate(int examNo, int questionOrder) {

        int count = questionDao.countByExamOrder(examNo,questionOrder);

        if (count > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 사용 중인 문제 순서입니다."
            );
        }
    }
    //수정용
    private void checkQuestionOrderDuplicate(int examNo, int questionOrder, int questionNo) {

        int count = questionDao.countByExamOrderExcludeSelf(
                        examNo,
                        questionOrder,
                        questionNo
                );

        if (count > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 사용 중인 문제 순서입니다."
            );
        }
    }

    // 문제 등록
    @Override
    public int insert(
            QuestionDto questionDto,
            List<MultipartFile> files,
            int employeeNo,
            boolean tutor)
            throws IllegalStateException, IOException {
    	//시험 존재 여부 + 작성자 + 작성중 상태 확인
    	checkEditableExam(questionDto.getExamNo(), employeeNo, tutor);
    	
    	//문제 순서 중복 확인
    	checkQuestionOrderDuplicate(questionDto.getExamNo(), questionDto.getQuestionOrder());
        // 문제 번호 생성
        int questionNo = questionDao.sequence();
        questionDto.setQuestionNo(questionNo);
        
        // 문제 등록
        questionDao.insert(questionDto);
        
        // 첨부파일 등록
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // attach DB + 실제 파일 저장
                    int attachNo = attachService.save(file);
                    // 문제와 첨부파일 연결
                    questionDao.connect(questionNo, attachNo);
                }
            }
        }

        return questionNo;
    }

    // 문제 단일 조회
    @Override
    public QuestionDto selectOne(int questionNo) {

        QuestionDto questionDto = questionDao.selectOne(questionNo);
        
        if (questionDto == null) {
            throw new TargetNotfoundException();
        }

        return questionDto;
    }

    // 특정 시험의 문제 목록 조회
    @Override
    public List<QuestionDto> selectListByExam(int examNo) {
        return questionDao.selectListByExam(examNo);
    }
    
    //학생용 문제 조회 구현
    @Override
	public List<StudentQuestionVO> selectListByAttempt(int attemptNo, int studentNo) {
    	// 1. 응시정보 확인
        AttemptDto attempt = attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        // 2. 본인 응시인지 확인
        if (attempt.getStudentNo() != studentNo) {
            throw new GetOutException();
        }

        // 3. 이미 제출한 시험이면 응시화면 접근 금지
        if ("제출완료".equals(attempt.getAttemptStatus())) {
            throw new GetOutException();
        }

        // 4. 해당 시험 문제 목록
        List<QuestionDto> questionList = questionDao.selectListByExam(attempt.getExamNo());

        List<StudentQuestionVO> result = new ArrayList<>();

        for (QuestionDto question : questionList) {
            // =========================
            // 보기 조회
            // =========================
            List<QuestionOptionDto> optionList =
                    questionOptionDao.selectListByQuestion(
                            question.getQuestionNo()
                    );

            // 정답 여부를 제거하고 학생용 VO로 변환
            List<StudentQuestionOptionVO> studentOptionList =
                    optionList.stream()
                            .map(option ->
                                    StudentQuestionOptionVO.builder()
                                            .optionNo(option.getOptionNo())
                                            .optionContent(option.getOptionContent())
                                            .optionOrder(option.getOptionOrder())
                                    .build())
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
            // 학생용 문제 VO 조립
            // =========================

            StudentQuestionVO studentQuestion =
                    StudentQuestionVO.builder()
                            .questionNo(question.getQuestionNo())
                            .examNo(question.getExamNo())
                            .questionContent(question.getQuestionContent())
                            .questionScore(question.getQuestionScore())
                            .questionOrder(question.getQuestionOrder())
                            .optionList(studentOptionList)
                            .fileList(fileList)
                    .build();

            result.add(studentQuestion);
        }

        return result;
	}

    // 문제 수정
    @Override
    public boolean update(
            QuestionDto questionDto,
            List<MultipartFile> files,
            int employeeNo,
            boolean tutor)
            throws IllegalStateException, IOException {

        QuestionDto before = questionDao.selectOne(questionDto.getQuestionNo());
        
        if(before == null) {
        	throw new TargetNotfoundException();
        }
        
        //DB에 저장된 examNo 기준으로 검사
        checkEditableExam(before.getExamNo(), employeeNo, tutor);
        
        //문제 순서 중복 체크
        checkQuestionOrderDuplicate(before.getExamNo(), questionDto.getQuestionOrder(), questionDto.getQuestionNo());
        
        // 문제 기본정보 수정
        boolean result = questionDao.update(questionDto);

        // 신규 첨부파일 추가
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    int attachNo = attachService.save(file);
                    questionDao.connect(questionDto.getQuestionNo(), attachNo);
                }
            }
        }
        
        return result;
    }

    // 문제 삭제
    @Override
    public boolean delete(
            int questionNo,
            int employeeNo,
            boolean tutor) {

        QuestionDto question = questionDao.selectOne(questionNo);
        
        if(question == null) {
        	throw new TargetNotfoundException();
        }
        
        checkEditableExam(question.getExamNo(), employeeNo, tutor);

        // 문제 첨부파일 번호 미리 조회
        List<Integer> fileNos =questionDao.selectFiles(questionNo);

        // 문제 삭제
        boolean result = questionDao.delete(questionNo);

        // attach DB + 실제 파일 삭제
        for (Integer attachNo : fileNos) {
            attachService.delete(attachNo);
        }
        
        return result;
    }

    // 문제 첨부파일 삭제
    @Override
    public void deleteFile(
            int questionNo,
            int attachNo,
            int employeeNo,
            boolean tutor) {

        QuestionDto question = questionDao.selectOne(questionNo);

        if (question == null) {
            throw new TargetNotfoundException();
        }

        checkEditableExam(question.getExamNo(), employeeNo, tutor);

        List<Integer> fileNos = questionDao.selectFiles(questionNo);

        if (!fileNos.contains(attachNo)) {
            throw new TargetNotfoundException();
        }

        attachService.delete(attachNo);
    }
}