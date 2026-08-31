package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractHistoryResponseVO {

    private long contractNo;
    private int employeeNo;

    private String wageType;
    private long baseWage;

    private Timestamp contractStart;
    private Timestamp contractEnd;

    private String contractStatus;

    private Timestamp signedTime;
    
    private Double breakTimes;
}