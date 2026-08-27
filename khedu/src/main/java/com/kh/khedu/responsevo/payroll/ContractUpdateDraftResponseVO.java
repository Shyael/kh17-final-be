package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class ContractUpdateDraftResponseVO {
	
	private String wageType;
	
	
	private long baseWage;
	
	
	private double dailyWorkHours;
	

	private double weeklyWorkHours;
	

	private Timestamp contractStart;
	
	private Timestamp contractEnd;
	
	
	private int payday;
	

	private String contractContent;
	
	private String contractStatus;
	
}
