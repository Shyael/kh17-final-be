package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContractChangeConditionResponseVO {
private String wageType;
	
	
	private long baseWage;
	
	
	private double dailyWorkHours;
	

	private double weeklyWorkHours;
	
	private Double breakMinutes;

	private Timestamp contractStart;
	
	private Timestamp contractEnd;
	
	
	private int payday;
	

	private String contractContent;
	
	private String contractStatus;
	
	private long contractNo;
	private int employeeNo;
}
