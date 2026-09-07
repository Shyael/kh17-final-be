package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AttemptDto;

public interface AttemptDao {
    // 응시 번호 시퀀스 생성
    int sequence();
    // 시험 응시 시작
    void insert(AttemptDto attemptDto);
    // 응시 번호로 단일 응시 조회
    AttemptDto selectOne(int attemptNo);
    // 특정 학생의 특정 시험 응시 조회
    AttemptDto selectOneByExamStudent(int examNo, int studentNo);
    // 시험 제출 처리
    boolean submit(AttemptDto attemptDto);
    //학생이름 조회
    String selectStudentName(int studentNo);
}