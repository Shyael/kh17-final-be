package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractHistoryResponseVO {

	
	private String accountName;
    private long contractNo;
    private int employeeNo;

    private String wageType;
    private long baseWage;

    private Timestamp contractStart;
    private Timestamp contractEnd;
    
    private String weeklyHolidayDay;

    private String contractStatus;

    private Timestamp signedTime;
    
    private double writtenBreakTimes;
}