package com.kh.khedu.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {
    private int attendanceNo;
    private int studentNo;
    private int classSessionNo;
    private Timestamp attendanceAt;
    private String attendanceState;

    // 상태 상수 (CHECK 제약조건 일치)
    public static final String STATE_UNCHECKED = "미출결";
    public static final String STATE_PRESENT   = "출석";
    public static final String STATE_ABSENT    = "결석";
    public static final String STATE_LATE      = "지각";
    public static final String STATE_LEAVE     = "조퇴";
}