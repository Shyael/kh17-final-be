package com.kh.khedu.responsevo.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEmployeeTeacherResponseVO {
	 private String employeeStatus;
	    private String employeeType;

	    private String accountName;
	    private String accountPhone;

	    private ContractTeacherResponseVO teacherSubject;
}
