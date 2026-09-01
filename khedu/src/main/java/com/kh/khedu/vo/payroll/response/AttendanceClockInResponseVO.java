package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceClockInResponseVO {
	private long empAttendanceNo;

    private Timestamp workDate;
    private Timestamp clockIn;

    private Double breakMinutes;

    private String workDayType;
    private int employeeNo;
    private String accountName;
}
