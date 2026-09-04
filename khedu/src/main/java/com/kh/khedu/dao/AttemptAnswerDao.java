package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AttemptAnswerDto;

public interface AttemptAnswerDao {
    // 답안 등록
    void insert(AttemptAnswerDto attemptAnswerDto);
    // 특정 응시의 특정 문제 답안 조회
    AttemptAnswerDto selectOne(int attemptNo, int questionNo);
    // 특정 응시의 전체 답안 목록 조회
    List<AttemptAnswerDto> selectListByAttempt(int attemptNo);
    // 답안 수정
    boolean update(AttemptAnswerDto attemptAnswerDto);
    // 특정 답안 삭제
    boolean delete(int attemptNo,int questionNo);
    // 특정 응시의 전체 답안 삭제
    boolean deleteByAttempt(int attemptNo);
}