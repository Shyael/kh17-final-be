package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContractUpdateDraftResponseVO {
	
	private String wageType;
	
	
	private long baseWage;
	
	
	private double dailyWorkHours;
	

	private double weeklyWorkHours;
	
	private Double writtenBreakMinutes;
	
	private Timestamp contractStart;
	
	private Timestamp contractEnd;
	
	
	private int payday;
	

	private String contractContent;
	
	private String contractStatus;
	
	private long contractNo;
	
}
