package com.kh.khedu.vo.payroll.request;


import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class DashboardPendingContractVO {

    private long contractNo;
    private int employeeNo;
    private String employeeName;
    private Timestamp contractStart;
    private boolean employeeSigned;
    private boolean employerSigned;
}