package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.QuestionOptionDto;

public interface QuestionOptionService {
    // 보기 등록
	int insert(
	        QuestionOptionDto questionOptionDto,
	        int employeeNo,
	        boolean tutor
	);
    // 보기 단일 조회
    QuestionOptionDto selectOne(int optionNo);
    // 특정 문제의 보기 목록 조회
    List<QuestionOptionDto> selectListByQuestion(int questionNo);
    // 보기 수정
    boolean update(
            QuestionOptionDto questionOptionDto,
            int employeeNo,
            boolean tutor
    );
    // 보기 삭제
    boolean delete(
            int optionNo,
            int employeeNo,
            boolean tutor
    );
    // 특정 문제의 보기 전체 삭제
    boolean deleteByQuestion(
            int questionNo,
            int employeeNo,
            boolean tutor
    );
}