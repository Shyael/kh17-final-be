package com.kh.khedu.requestvo.payroll;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractEmployeeSignRequestVO {
 @NotBlank
 private String employeeSignature;
 
}
