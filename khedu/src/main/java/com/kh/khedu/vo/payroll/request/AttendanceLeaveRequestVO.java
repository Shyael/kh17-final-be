package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AttendanceLeaveRequestVO {

    @Positive
    private int employeeNo;

    @NotNull
    private Timestamp workDate;
}