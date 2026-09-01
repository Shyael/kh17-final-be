package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AttendanceAbsentRequestVO {

    @Positive
    private long employeeNo;

    @NotNull
    private Timestamp workDate;
}