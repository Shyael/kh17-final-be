package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ExamDto;

public interface ExamDao {
    // 시험 번호 시퀀스 생성
    int sequence();
    // 시험 등록
    void insert(ExamDto examDto);
    // 시험 번호로 단일 시험 조회
    ExamDto selectOne(int examNo);
    // 전체 시험 목록 조회
    List<ExamDto> selectList();
    // 특정 강의의 시험 목록 조회
    List<ExamDto> selectListByCourse(int courseNo);
    // 특정 강사가 등록한 시험 목록 조회
    List<ExamDto> selectListByEmployee(int employeeNo);
    // 시험 정보 수정
    boolean update(ExamDto examDto);
    // 시험 삭제
    boolean delete(int examNo);
}