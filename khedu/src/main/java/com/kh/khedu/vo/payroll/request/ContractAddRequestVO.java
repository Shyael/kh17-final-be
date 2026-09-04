package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ContractAddRequestVO {
	@NotNull
	@Positive
	// 직원 번호
	private int employeeNo;

	@NotBlank
	@Pattern(regexp = "monthly|hourly|daily")
	// 급여 형태
	// monthly / hourly / daily
	private String wageType;

	@NotNull
	
	// 기준 임금
	private long baseWage;
	@NotNull
	
	// 1일 소정근로시간
	private double dailyWorkHours;
	@NotNull
	
	// 주 소정근로시간
	private double weeklyWorkHours;
	@NotNull
	// 계약 시작일
	private Timestamp contractStart;
	@NotNull
	// 계약 종료일
	private Timestamp contractEnd;
	@NotNull
	@Min(1)
	@Max(31)
	// 매월 급여 지급 예정일
	private int payday;
	@NotBlank
	// 계약서 본문
	private String contractContent;
	
	@PositiveOrZero
	private double writtenBreakMinutes;
}
