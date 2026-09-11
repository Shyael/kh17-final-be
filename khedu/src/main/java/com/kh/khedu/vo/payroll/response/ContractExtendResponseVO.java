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
public class ContractExtendResponseVO {
	 private long contractNo;

	    private Timestamp contractStart;

	    private Timestamp contractEnd;

	    private String contractStatus;
}
