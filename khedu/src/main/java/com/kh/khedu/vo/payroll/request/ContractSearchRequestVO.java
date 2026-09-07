package com.kh.khedu.vo.payroll.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractSearchRequestVO {

    private Integer employeeNo;
    private String accountId;
    private String accountName;
    private String accountPhone;
}