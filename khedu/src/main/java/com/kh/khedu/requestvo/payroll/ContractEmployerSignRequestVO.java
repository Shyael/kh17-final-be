package com.kh.khedu.requestvo.payroll;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ContractEmployerSignRequestVO {
	 @NotBlank
	 private String employerSignature;
}
