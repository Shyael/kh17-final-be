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

    // 3. 보호자 및 주소 (현재 DB에 없으므로 쿼리에서 임시 하드코딩할 예정)
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String address;

    // 4. 수납 및 상태 정보 (임시 하드코딩)
    private int tuition;          // 이번 달 수강료
    private boolean isPaid;       // 납부 여부 (true: 완납, false: 미납)
}