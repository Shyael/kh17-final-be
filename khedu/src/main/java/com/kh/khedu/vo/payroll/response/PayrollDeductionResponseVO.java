package com.kh.khedu.vo.payroll.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDeductionResponseVO {

	private String deductionType;

	private Long baseAmount;

	private Double deductionRate;

	private Long fixedDeductionAmount;

	private Long deductionAmount;

	private Integer deductionBasisYear;

	private String deductionNote;
}