package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractSignResponseVO {

	private long contractNo;

	private String employeeSignature;

	private String employerSignature;

	private Timestamp signedTime;
}
