package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollListResponseVO {

	private Long payrollNo;

	private Integer payrollYear;

	private Integer payrollMonth;

	private Double totalWorkHours;

	private Long grossPay;

	private Long totalDeduction;

	private Long netPay;

	private String payrollStatus;

	private Timestamp calculatedAt;

	private Timestamp confirmedAt;

	// 현재 실제 지급되어 있는 금액
	// paid 총액 - cancelled 총액
	private Long currentPaidAmount;
}
