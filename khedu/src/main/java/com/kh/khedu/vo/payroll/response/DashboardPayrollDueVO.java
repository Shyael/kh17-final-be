package com.kh.khedu.vo.payroll.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPayrollDueVO {

    private long payrollNo;
    private int employeeNo;
    private String employeeName;

    private int payrollYear;
    private int payrollMonth;

    private LocalDate payday;

    private long netPay;
    private String paymentStatus;

    private long dDay;
}