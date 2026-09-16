package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleUpdateRequestVO {

    @Positive
    private long workScheduleNo;

    private Timestamp scheduledWorkDate;

    private Timestamp scheduledClockIn;

    private Timestamp scheduledClockOut;

    private String scheduledDayType;


    @PositiveOrZero
    private Double actualWorkHours;

    @PositiveOrZero
    private Double actualOvertimeHours;

    @PositiveOrZero
    private Double actualNightHours;

    @PositiveOrZero
    private Double actualHolidayHours;
}