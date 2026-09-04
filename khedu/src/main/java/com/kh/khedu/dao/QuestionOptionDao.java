package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.QuestionOptionDto;

public interface QuestionOptionDao {
    // 보기 번호 시퀀스 생성
    int sequence();
    // 보기 등록
    void insert(QuestionOptionDto questionOptionDto);
    // 보기 번호로 단일 보기 조회
    QuestionOptionDto selectOne(int optionNo);
    // 특정 문제의 보기 목록 조회
    List<QuestionOptionDto> selectListByQuestion(int questionNo);
    // 보기 수정
    boolean update(QuestionOptionDto questionOptionDto);
    // 보기 삭제
    boolean delete(int optionNo);
    // 특정 문제의 보기 전체 삭제
    boolean deleteByQuestion(int questionNo);
}