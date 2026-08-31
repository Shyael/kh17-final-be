package com.kh.khedu.vo.payroll.request;


import java.sql.Timestamp;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class ContractUpdateDraftRequestVO {
	@NotBlank
	@Pattern(regexp = "monthly|hourly|daily")
	private String wageType;
	
	@NotNull
	@DecimalMin("0")
	private long baseWage;
	
	@NotNull
	@DecimalMin("0.5")
	private double dailyWorkHours;
	
	@NotNull
	@DecimalMin("0.5")
	private double weeklyWorkHours;
	
	@NotNull
	private Timestamp contractStart;
	
	private Timestamp contractEnd;
	
	
	@Min(1)
	@Max(31)
	private int payday;
	
	@NotBlank
	private String contractContent;
	
	private long contractNo;
	
	@PositiveOrZero
	private Double writtenBreakMinutes;
}
