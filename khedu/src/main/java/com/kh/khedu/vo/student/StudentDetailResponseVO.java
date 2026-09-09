package com.kh.khedu.vo.student;

import lombok.Data;

@Data
public class StudentDetailResponseVO {
    // 1. 식별자 및 기본 정보 (account 조인)
    private int studentNo;        // SID
    private int accountNo;
    private String studentName;   // 이름 (account_name)
    private String studentPhone;  // 연락처 (account_phone)
    private String studentEmail;  // 이메일 (account_id)

    // 2. 학생 고유 정보 (student 테이블)
    private String studentSchool; // 학교
    private String studentGrade;  // 학년
    private String studentGender; // 성별
    private String studentEtc;    // 특이사항 (remarks)
    private String studentAcademicStatus;
    
    //출석률 정보
    private int attendanceRate;

}