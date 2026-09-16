package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WorkScheduleAddRequestVO {

    @Positive
    private int employeeNo;

    @NotNull
    private Timestamp scheduledWorkDate;

    private Timestamp scheduledClockIn;

    private Timestamp scheduledClockOut;

    @NotNull
    private String scheduledDayType;


    // Service에서 설정
    private long contractNo;

    // Service에서 설정
    private long workScheduleNo;
}