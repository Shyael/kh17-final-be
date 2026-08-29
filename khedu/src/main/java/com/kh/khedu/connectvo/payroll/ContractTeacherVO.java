package com.kh.khedu.connectvo.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor    @AllArgsConstructor
public class ContractTeacherVO {
private String employeeType;
private int tutorNo;
}
