package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceTodayResponseVO {
    private Long empAttendanceNo;
    private Long workScheduleNo;
    private Timestamp workDate;
    private String workDayType;
    private String attendanceType;
    private Timestamp clockIn;
    private Timestamp clockOut;
    private int breakMinutes;
    private double nightHours;
    private double overtimeHours;
}