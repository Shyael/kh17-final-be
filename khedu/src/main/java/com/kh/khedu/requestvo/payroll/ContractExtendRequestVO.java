package com.kh.khedu.requestvo.payroll;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContractExtendRequestVO {
	private long contractNo;
	
	@NotNull
	private Timestamp contractEnd;
}
