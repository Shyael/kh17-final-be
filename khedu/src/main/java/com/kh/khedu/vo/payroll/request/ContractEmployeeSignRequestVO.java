package com.kh.khedu.vo.payroll.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractEmployeeSignRequestVO {
 @NotBlank
 private String employeeSignature;
 private long contractNo;
}
