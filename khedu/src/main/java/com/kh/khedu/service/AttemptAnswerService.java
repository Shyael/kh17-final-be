package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.AttemptAnswerDto;

public interface AttemptAnswerService {

    // 답안 등록
	void insert(
	        AttemptAnswerDto attemptAnswerDto,
	        int studentNo
	);
    
    // 특정 응시의 전체 답안 목록 조회
	List<AttemptAnswerDto> selectListByAttempt(
	        int attemptNo,
	        int studentNo
	);
    
    // 답안 수정
    boolean update(
            AttemptAnswerDto attemptAnswerDto,
            int studentNo
    );
    // 특정 답안 삭제
    boolean delete(
            int attemptNo,
            int questionNo,
            int studentNo
    );
}