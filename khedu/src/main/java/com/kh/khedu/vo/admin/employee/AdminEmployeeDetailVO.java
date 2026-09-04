package com.kh.khedu.vo.admin.employee;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.vo.roles.RoleVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="관리자 직원 정보 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminEmployeeDetailVO {
	// employee
    private int employeeNo;
    private String employeeType;
    private Timestamp employeeHtime;
    private String employeeStatus; // 데스크/강사/원장
    // account
    private int accountNo;
    private String accountId;
    private String accountBirth;
    private String accountName;
    private String accountPhone;
    private String accountStatus;
    private String accountType;
    private Timestamp accountUtime;
    //roles
    private List<RoleVO> roles;
}
