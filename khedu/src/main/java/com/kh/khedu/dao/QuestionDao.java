package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.QuestionDto;

public interface QuestionDao {
    // 문제 번호 시퀀스 생성
    int sequence();
    // 문제 등록
    void insert(QuestionDto questionDto);
    // 문제 번호로 단일 문제 조회
    QuestionDto selectOne(int questionNo);
    // 특정 시험의 문제 목록 조회
    List<QuestionDto> selectListByExam(int examNo);
    // 문제 수정
    boolean update(QuestionDto questionDto);
    // 문제 삭제
    boolean delete(int questionNo);
    // 문제와 첨부파일 연결
    void connect(int questionNo, int attachNo);
    // 특정 문제의 첨부파일 번호 목록 조회
    List<Integer> selectFiles(int questionNo);
}