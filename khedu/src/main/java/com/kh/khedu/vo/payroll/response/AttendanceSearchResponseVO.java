package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceSearchResponseVO {
	    private long empAttendanceNo;
	    private long contractNo;
	    private int employeeNo;
	    private Timestamp workDate;

	    private Timestamp clockIn;
	    private Timestamp clockOut;

	    private Double breakMinutes;

	    private String attendanceType;
	    private String scheduledWorkDayType;

	    private double nightHours;
	    private double overtimeHours;
}
