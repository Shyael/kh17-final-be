package com.kh.khedu.vo.employee;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="직원 정보 조회용")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeDetailVO {
	// employee
    private int employeeNo;
    private String employeeType;
    private Timestamp employeeHtime;

    // account
    private int accountNo;
    private String accountId;
    private String accountName;
    private String accountPhone;
    private String accountStatus;
    private String accountType;
}
