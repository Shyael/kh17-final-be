package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContractExtendRequestVO {
	private long contractNo;
	
	@NotNull
	private Timestamp contractEnd;
}
