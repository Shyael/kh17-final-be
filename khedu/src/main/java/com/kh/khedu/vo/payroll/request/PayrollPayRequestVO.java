package com.kh.khedu.vo.payroll.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPayRequestVO {

	private Integer employeeNo;

	private Integer payrollYear;

	private Integer payrollMonth;

	private String paymentMethod;

	private String paymentNote;
}