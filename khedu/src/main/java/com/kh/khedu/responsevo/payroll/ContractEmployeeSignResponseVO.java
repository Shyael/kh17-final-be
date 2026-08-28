package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ContractEmployeeSignResponseVO {
	private long contractNo;

    private String employeeSignature;

  

    private Timestamp signedTime;

    private String contractStatus;
}
