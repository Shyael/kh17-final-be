package com.kh.khedu.responsevo.payroll;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailResponseVO {

    private long contractNo;
    private int employeeNo;

    private String wageType;
    private long baseWage;

    private double dailyWorkHours;
    private double weeklyWorkHours;
    private Double breakMinutes;
    
    private Timestamp contractStart;
    private Timestamp contractEnd;

    private int payday;

    private String contractContent;

    private String contractStatus;

    private boolean employeeSigned;
    private boolean employerSigned;

    private Timestamp signedTime;
}