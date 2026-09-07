package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AttemptAnswerDao;
import com.kh.khedu.dto.AttemptAnswerDto;
import com.kh.khedu.error.TargetNotfoundException;

@Service
@Transactional
public class AttemptAnswerServiceImpl implements AttemptAnswerService {

    @Autowired
    private AttemptAnswerDao attemptAnswerDao;

    // 답안 등록
    @Override
    public void insert(AttemptAnswerDto attemptAnswerDto) {

        attemptAnswerDao.insert(attemptAnswerDto);
    }

    // 특정 응시의 특정 문제 답안 조회
    @Override
    public AttemptAnswerDto selectOne(int attemptNo,int questionNo) {
        AttemptAnswerDto answer = attemptAnswerDao.selectOne(attemptNo,questionNo);

        if (answer == null) {
            throw new TargetNotfoundException();
        }

        return answer;
    }

    // 특정 응시의 전체 답안 목록 조회
    @Override
    public List<AttemptAnswerDto> selectListByAttempt(int attemptNo) {

        return attemptAnswerDao.selectListByAttempt(attemptNo);
    }

    // 답안 수정
    @Override
    public boolean update(AttemptAnswerDto attemptAnswerDto) {

        // 기존 답안 존재 확인
        AttemptAnswerDto findAnswer = attemptAnswerDao.selectOne(attemptAnswerDto.getAttemptNo(), attemptAnswerDto.getQuestionNo());

        if (findAnswer == null) {
            throw new TargetNotfoundException();
        }

        return attemptAnswerDao.update(attemptAnswerDto);
    }

    // 특정 답안 삭제
    @Override
    public boolean delete(int attemptNo, int questionNo) {

        // 답안 존재 확인
        AttemptAnswerDto answer = attemptAnswerDao.selectOne(attemptNo, questionNo);

        if (answer == null) {
            throw new TargetNotfoundException();
        }

        return attemptAnswerDao.delete(attemptNo,questionNo);
    }

    // 특정 응시의 전체 답안 삭제
    @Override
    public boolean deleteByAttempt(int attemptNo) {

        return attemptAnswerDao.deleteByAttempt(attemptNo);
    }
}