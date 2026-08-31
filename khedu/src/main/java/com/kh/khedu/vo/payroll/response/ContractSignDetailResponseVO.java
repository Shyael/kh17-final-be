package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContractSignDetailResponseVO {
	private long contractNo;

    private long employeeNo;

    private String wageType;

    private long baseWage;

    private double dailyWorkHours;

    private double weeklyWorkHours;
    private Double writtenBreakMinutes;
    private Timestamp contractStart;

    private Timestamp contractEnd;

    private int payday;

    private String contractContent;

    private String employeeSignature;

    private String employerSignature;

    private Timestamp signedTime;

    private String contractStatus;
}
