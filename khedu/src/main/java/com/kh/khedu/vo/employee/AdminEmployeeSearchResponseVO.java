package com.kh.khedu.vo.employee;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminEmployeeSearchResponseVO {
	   // 직원
    private Long employeeNo;
    private String employeeType;
    private Timestamp employeeHtime;
    private String employeeStatus;

    // 계정
    private String accountId;
    private String accountName;
    private String accountPhone;
    private String accountBirth;
    private String accountStatus;
    private Timestamp accountCdate;
}
