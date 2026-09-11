package com.kh.khedu.vo.payroll.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPaymentCancelRequestVO {

	private Integer employeeNo;

	private Integer payrollYear;

	private Integer payrollMonth;

	private Long  cancelAmount;

	private String paymentNote;
}