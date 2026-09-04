package com.kh.khedu.dto.payroll;


import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPaymentDto {

	private Long payrollPaymentNo;
	private Long payrollNo;

	private Long paymentAmount;
	private Timestamp paymentAt;

	private String paymentMethod;
	private String paymentNote;

	private String paymentStatus;

	private Long cancelTargetPaymentNo;

}