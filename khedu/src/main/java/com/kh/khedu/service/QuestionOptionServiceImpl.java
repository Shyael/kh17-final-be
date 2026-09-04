package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;

@Service
@Transactional
public class QuestionOptionServiceImpl
        implements QuestionOptionService {

    @Autowired
    private QuestionOptionDao questionOptionDao;
    
    @Autowired
    private QuestionDao questionDao;
    
    @Autowired
    private ExamDao examDao;
    
    //공통 메서드
    //공개시 수정 불가
    private QuestionDto checkEditableQuestion(int questionNo, int employeeNo, boolean tutor) {
        QuestionDto question = questionDao.selectOne(questionNo);

        if (question == null) {
            throw new TargetNotfoundException();
        }

        ExamDto exam = examDao.selectOne(question.getExamNo());

        if (exam == null) {
            throw new TargetNotfoundException();
        }

        // 강사는 본인 시험만 관리
        if (tutor && exam.getEmployeeNo() != employeeNo) {
            throw new GetOutException();
        }

        // 작성중 시험만 보기 편집 가능
        if (!"작성중".equals(exam.getExamStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "공개된 시험의 보기는 수정할 수 없습니다."
            );
        }

        return question;
    }
    //순서 중복 검사
    private void checkOptionOrderDuplicate(int questionNo, int optionOrder) {

        int count =questionOptionDao.countByQuestionOrder(questionNo, optionOrder);
        
        if (count > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 사용 중인 보기 순서입니다."
            );
        }
    }
    
    private void checkOptionOrderDuplicate(int questionNo, int optionOrder, int optionNo) {
        int count = questionOptionDao.countByQuestionOrderExcludeSelf(
                        questionNo,
                        optionOrder,
                        optionNo
                );

        if (count > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 사용 중인 보기 순서입니다."
            );
        }
    }
    
    // 보기 등록
    @Override
    public int insert(
            QuestionOptionDto questionOptionDto,
            int employeeNo,
            boolean tutor) {
    	//시험 작성중일때만 입력가능
    	checkEditableQuestion(questionOptionDto.getQuestionNo(), employeeNo, tutor);
    	//지문 순서 중복 검사
    	checkOptionOrderDuplicate(questionOptionDto.getQuestionNo(), questionOptionDto.getOptionOrder());
        // 보기 번호 생성
        int optionNo = questionOptionDao.sequence();
        
        questionOptionDto.setOptionNo(optionNo);
        
        // 보기 등록
        questionOptionDao.insert(questionOptionDto);

        return optionNo;
    }

    // 보기 단일 조회
    @Override
    public QuestionOptionDto selectOne(int optionNo) {

        QuestionOptionDto option = questionOptionDao.selectOne(optionNo);

        if (option == null) {
            throw new TargetNotfoundException();
        }

        return option;
    }

    // 특정 문제의 보기 목록 조회
    @Override
    public List<QuestionOptionDto> selectListByQuestion(int questionNo) {
    
        return questionOptionDao.selectListByQuestion(questionNo);
    }

    // 보기 수정
    @Override
    public boolean update(
            QuestionOptionDto questionOptionDto,
            int employeeNo,
            boolean tutor) {

        QuestionOptionDto before =questionOptionDao.selectOne(questionOptionDto.getOptionNo());

        if (before == null) {
            throw new TargetNotfoundException();
        }

        // DB에 저장된 실제 questionNo 기준
        checkEditableQuestion(before.getQuestionNo(),employeeNo,tutor);

        checkOptionOrderDuplicate(before.getQuestionNo(), questionOptionDto.getOptionOrder(), questionOptionDto.getOptionNo());

        return questionOptionDao.update(questionOptionDto);
    }

    // 보기 삭제
    @Override
    public boolean delete(
            int optionNo,
            int employeeNo,
            boolean tutor) {

        QuestionOptionDto option = questionOptionDao.selectOne(optionNo);

        if (option == null) {
            throw new TargetNotfoundException();
        }

        checkEditableQuestion( option.getQuestionNo(), employeeNo, tutor);

        return questionOptionDao.delete(optionNo);
    }

    // 특정 문제의 보기 전체 삭제
    @Override
    public boolean deleteByQuestion(
            int questionNo,
            int employeeNo,
            boolean tutor) {

        // 문제 존재 여부
        // 강사 본인 시험 여부
        // 작성중 시험 여부
        checkEditableQuestion( questionNo, employeeNo, tutor);

        return questionOptionDao.deleteByQuestion(questionNo);
    }
}