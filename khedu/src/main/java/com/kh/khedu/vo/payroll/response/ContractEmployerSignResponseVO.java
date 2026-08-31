package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ContractEmployerSignResponseVO {
	private long contractNo;

   
    private String employerSignature;

    private Timestamp signedTime;

    private String contractStatus;
}
