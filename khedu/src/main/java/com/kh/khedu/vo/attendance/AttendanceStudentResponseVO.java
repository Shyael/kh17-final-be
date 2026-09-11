package com.kh.khedu.vo.attendance;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStudentResponseVO {
    private int attendanceNo;          // 출결 PK (수동 정정 시 사용)
    private int studentNo;             // 학생 번호
    private String studentName;        // 학생 이름 (account_name)
    private String studentPhone;       // 학생 연락처 (account_phone)
    private String attendanceState;    // 상태 ('미출결', '출석', '지각', '조퇴', '결석')
    private Timestamp attendanceAt;    // 출결 태그/수정 시각
}