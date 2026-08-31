package com.kh.khedu.vo.payroll.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ContractEmployerSignRequestVO {
	 @NotBlank
	 private String employerSignature;
	 private long contractNo;
}
