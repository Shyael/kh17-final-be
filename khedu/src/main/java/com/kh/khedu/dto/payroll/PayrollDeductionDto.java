package com.kh.khedu.dto.payroll;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDeductionDto {

	private Long deductionNo;
	private Long payrollNo;

	private String deductionType;

	private Long baseAmount;
	private Double deductionRate;
	private Long fixedDeductionAmount;

	private Long deductionAmount;

	private Integer deductionBasisYear;

	private String deductionNote;

}