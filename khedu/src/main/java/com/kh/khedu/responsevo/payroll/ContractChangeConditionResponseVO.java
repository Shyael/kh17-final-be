package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import lombok.Data;
@Data
public class ContractChangeConditionResponseVO {
private String wageType;
	
	
	private long baseWage;
	
	
	private double dailyWorkHours;
	

	private double weeklyWorkHours;
	

	private Timestamp contractStart;
	
	private Timestamp contractEnd;
	
	
	private int payday;
	

	private String contractContent;
	
	private String contractStatus;
	
	private long contractNo;
}
