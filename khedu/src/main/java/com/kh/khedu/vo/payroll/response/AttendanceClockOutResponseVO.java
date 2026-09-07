package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceClockOutResponseVO {
    private long empAttendanceNo;

    private Timestamp clockIn;
    private Timestamp clockOut;

    private Double breakMinutes;

    private double nightHours;
    private double overtimeHours;
    private String scheduledWorkDayType;
}
