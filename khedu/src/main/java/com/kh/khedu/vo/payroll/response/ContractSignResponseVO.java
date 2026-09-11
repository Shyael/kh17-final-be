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
public class ContractSignResponseVO {

	private long contractNo;

	private boolean employeeSigned;

	private boolean employerSigned;

	private Timestamp signedTime;
	
	private String employeeSignature;
	
	private String employerSignature;
}
