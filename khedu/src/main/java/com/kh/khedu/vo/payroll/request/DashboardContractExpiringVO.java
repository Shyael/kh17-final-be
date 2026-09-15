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
public class DashboardContractExpiringVO {

    private long contractNo;
    private int employeeNo;
    private String employeeName;
    private Timestamp contractEnd;
    private long dDay;
}