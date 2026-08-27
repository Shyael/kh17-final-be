package com.kh.khedu.requestvo.payroll;


import java.sql.Timestamp;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class ContractUpdateRequestVO {
	@NotBlank
	@Pattern(regexp = "monthly|hourly|daily")
	private String wageType;
	
	@NotNull
	@DecimalMin("0")
	private long baseWage;
	
	@NotNull
	@DecimalMin("0")
	private int dailyWorkHours;
	
	@NotNull
	@DecimalMin("0")
	private int weeklyWorkHours;
	
	@NotNull
	private Timestamp contractStart;
	
	private Timestamp contractEnd;
	
	@NotNull
	@Min(1)
	@Max(31)
	private int payday;
	
	@NotBlank
	private String contractContent;
}
