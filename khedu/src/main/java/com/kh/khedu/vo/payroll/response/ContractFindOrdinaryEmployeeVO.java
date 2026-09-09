package com.kh.khedu.vo.payroll.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContractFindOrdinaryEmployeeVO {
	private int employeeNo;
	private double weeklyWorkHours;
}
