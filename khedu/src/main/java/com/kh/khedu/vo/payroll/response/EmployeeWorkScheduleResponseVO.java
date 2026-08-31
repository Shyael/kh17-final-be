package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeWorkScheduleResponseVO {
    private long workScheduleNo;
    private long employeeNo;
    private Timestamp scheduleDate;
    private String scheduleDayType;
}