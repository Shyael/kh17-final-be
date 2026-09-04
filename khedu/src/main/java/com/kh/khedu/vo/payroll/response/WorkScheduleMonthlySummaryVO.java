package com.kh.khedu.vo.payroll.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleMonthlySummaryVO {

    private double totalWorkHours;
    private double totalOvertimeHours;
    private double totalNightHours;
    private double totalHolidayHours;
}