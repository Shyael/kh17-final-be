package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.AttemptDto;

public interface AttemptService {
    // 시험 응시 시작
    int insert(AttemptDto attemptDto);
    // 응시 번호로 단일 응시 조회
    AttemptDto selectOne(int attemptNo);
    // 특정 학생의 특정 시험 응시 조회
    AttemptDto selectOneByExamStudent(
            int examNo,
            int studentNo
    );
    // 특정 시험의 전체 응시 목록 조회
    List<AttemptDto> selectListByExam(int examNo);
    // 특정 학생의 전체 응시 목록 조회
    List<AttemptDto> selectListByStudent(int studentNo);
    // 시험 제출
    boolean submit(AttemptDto attemptDto);
    // 응시 상태 수정
    boolean updateStatus(AttemptDto attemptDto);
    // 응시 삭제
    boolean delete(int attemptNo);
}