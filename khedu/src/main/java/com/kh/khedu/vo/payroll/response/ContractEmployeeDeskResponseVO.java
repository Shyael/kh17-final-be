package com.kh.khedu.vo.payroll.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEmployeeDeskResponseVO {

    private String employeeStatus;
    private String employeeType;

    private String accountName;
    private String accountPhone;
}
