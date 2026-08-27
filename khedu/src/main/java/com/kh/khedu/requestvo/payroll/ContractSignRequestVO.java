package com.kh.khedu.requestvo.payroll;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractSignRequestVO {
 @NotBlank
 private String employeeSignature;
 @NotBlank
 private String employerSignature;
}
