package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.vo.exam.ExamResultVO;

public interface AttemptService {
    // 시험 응시 시작
    int insert(AttemptDto attemptDto);
    
    // 시험 제출
    boolean submit(
            int attemptNo,
            int studentNo
    );
    //응시 가능 시간 + 개인 제한시간 검사
    AttemptDto checkAvailableAttempt(
            int attemptNo,
            int studentNo
    );
    //시험 결과 VO(학생용)
    ExamResultVO selectResult(
            int attemptNo,
            int studentNo
    );
    //시험 결과 VO(강사용)
    ExamResultVO selectResultByManage(
            int attemptNo,
            int employeeNo,
            boolean tutor
    );
}