package com.kh.khedu.connectvo.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor    @AllArgsConstructor
public class ContractStatusAndAccountNoVO {
private String employeeType;
private int accountNo;
	
}
