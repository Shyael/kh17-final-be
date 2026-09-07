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
public class ContractSearchResponseVO {
	private long contractNo;
	private int employeeNo;

	private String accountId;
	private String accountName;
	private String accountPhone;

	private String contractStatus;
	private Timestamp contractStart;
	private Timestamp contractEnd;
}
