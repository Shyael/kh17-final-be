package com.kh.khedu.vo.payroll.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttendanceClockInRequestVO {
    @NotBlank
    private String workDayType;
}