package com.kh.khedu.vo.payroll.request;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPayrollDueQueryVO {

    private long payrollNo;
    private int employeeNo;
    private String employeeName;

    private int payrollYear;
    private int payrollMonth;

    private Integer payday;

    private long netPay;
    private String paymentStatus;

    
}