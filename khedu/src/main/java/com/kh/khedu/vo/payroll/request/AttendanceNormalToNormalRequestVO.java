package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AttendanceNormalToNormalRequestVO {
    @Positive
    @NotNull
    private Long empAttendanceNo;

    @NotNull
    private Timestamp clockIn;

    @NotNull
    private Timestamp clockOut;

    @NotNull
    @PositiveOrZero
    private Double breakMinutes;

}
