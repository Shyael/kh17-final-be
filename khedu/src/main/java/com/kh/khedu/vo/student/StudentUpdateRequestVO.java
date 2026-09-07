package com.kh.khedu.vo.student;

import lombok.Data;

@Data
public class StudentUpdateRequestVO {
    // 필수 식별자 (어떤 학생을 수정할 것인가?)
    private int studentNo;
    private int accountNo; // account 테이블도 수정해야 하므로 필요합니다.

    // 수정 가능한 account 정보
    private String studentPhone;
    private String studentEmail;

    // 수정 가능한 student 정보
    private String studentSchool;
    private String studentGrade;
    private String studentGender;
    private String studentEtc;
}