package com.kh.khedu.vo.payroll.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollMonthlyListResponseVO {

	private Long payrollNo;

	private int employeeNo;

	private String accountName;

	private int payrollYear;

	private int payrollMonth;

	private Double totalWorkHours;

	private Long grossPay;

	private Long totalDeduction;

	private Long netPay;

	private String payrollStatus;

	private Long currentPaidAmount;
}