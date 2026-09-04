package com.kh.khedu.dto.payroll;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeAttendanceDto {
    private Long empAttendanceNo;
    private long contractNo;
    private Timestamp workDate;
    private Timestamp clockIn;
    private Timestamp clockOut;
    private Double breakMinutes;
    private String attendanceType;
    private double nightHours;
    private double overtimeHours;
	
}