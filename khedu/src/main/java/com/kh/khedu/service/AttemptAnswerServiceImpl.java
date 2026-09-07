package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.kh.khedu.dao.AttemptAnswerDao;
import com.kh.khedu.dao.AttemptDao;
import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.AttemptAnswerDto;
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;

@Service
@Transactional
public class AttemptAnswerServiceImpl implements AttemptAnswerService {

    @Autowired
    private AttemptAnswerDao attemptAnswerDao;
    
    @Autowired
    private QuestionOptionDao questionOptionDao;
    
    @Autowired
    private AttemptDao attemptDao;

    @Autowired
    private QuestionDao questionDao;
    
    @Autowired
    private AttemptService attemptService;

    //공통 메서드
    private void checkOption(int questionNo, Integer optionNo) {
        // 미응답은 허용
        if (optionNo == null) {
            return;
        }

        QuestionOptionDto option = questionOptionDao.selectOne(optionNo);

        if (option == null) {
            throw new TargetNotfoundException();
        }

        // 선택한 보기가 해당 문제의 보기인지 확인
        if (option.getQuestionNo() != questionNo) {
            throw new GetOutException();
        }
        
    }
    
    //questionNo가 해당 attempt의 시험 문제인지 검증
    private QuestionDto checkQuestionInAttempt(
            AttemptDto attempt,
            int questionNo) {
    	
        // 문제 조회
        QuestionDto question = questionDao.selectOne(questionNo);

        if (question == null) {
            throw new TargetNotfoundException();
        }

        // 해당 응시의 시험에 속한 문제인지 확인
        if (attempt.getExamNo() != question.getExamNo()) {
            throw new GetOutException();
        }

        return question;
    }
     
    // 답안 등록
    @Override
    public void insert(
            AttemptAnswerDto attemptAnswerDto,
            int studentNo) {
    	 // 본인 + 제출여부 + 시험시간 + 제한시간
        AttemptDto attempt = attemptService.checkAvailableAttempt(attemptAnswerDto.getAttemptNo(), studentNo);
     // 해당 문제가 이 응시 시험의 문제인지 확인
        checkQuestionInAttempt(attempt, attemptAnswerDto.getQuestionNo());
    	//선택한 보기가 해당 문제의 보기인지 확인
    	checkOption(attemptAnswerDto.getQuestionNo(), attemptAnswerDto.getOptionNo());
    	// 프론트에서 isCorrect를 보내도 무시
        attemptAnswerDto.setIsCorrect(null);
        attemptAnswerDao.insert(attemptAnswerDto);
    }
    
    // 특정 응시의 전체 답안 목록 조회
    @Override
    public List<AttemptAnswerDto> selectListByAttempt(
            int attemptNo,
            int studentNo) {

        // 본인 응시 + 제출여부 + 시험시간 검사
        attemptService.checkAvailableAttempt(attemptNo,studentNo);

        return attemptAnswerDao.selectListByAttempt(attemptNo);
    }

    // 답안 수정
    @Override
    public boolean update(
            AttemptAnswerDto attemptAnswerDto,
            int studentNo) {
    	// 본인 + 제출여부 + 시험시간 + 제한시간
    	AttemptDto attempt = attemptService.checkAvailableAttempt(attemptAnswerDto.getAttemptNo(), studentNo);
    	
        // 기존 답안 존재 확인
        AttemptAnswerDto findAnswer = attemptAnswerDao.selectOne(attemptAnswerDto.getAttemptNo(), attemptAnswerDto.getQuestionNo());

        if (findAnswer == null) {
            throw new TargetNotfoundException();
        }
     // 해당 문제가 이 응시 시험의 문제인지 확인
        checkQuestionInAttempt(attempt, attemptAnswerDto.getQuestionNo());
    	//선택한 보기가 해당 문제의 보기인지 확인
        checkOption(attemptAnswerDto.getQuestionNo(), attemptAnswerDto.getOptionNo());
        // 학생이 정답 여부를 조작할 수 없도록
        attemptAnswerDto.setIsCorrect(null);
        return attemptAnswerDao.update(attemptAnswerDto);
    }

    // 특정 답안 삭제
    @Override
    public boolean delete(
            int attemptNo,
            int questionNo,
            int studentNo) {
    	// 본인 + 제출여부 + 시험시간 + 제한시간
        attemptService.checkAvailableAttempt(attemptNo,studentNo);
        // 답안 존재 확인
        AttemptAnswerDto answer = attemptAnswerDao.selectOne(attemptNo, questionNo);

        if (answer == null) {
            throw new TargetNotfoundException();
        }

        return attemptAnswerDao.delete(attemptNo,questionNo);
    }
}