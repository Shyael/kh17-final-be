package com.kh.khedu.vo.admin.employee;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "관리자 직원 목록 조회")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminEmployeeListVO {
	private int employeeNo;
	private String accountName;
	private String accountId;
	private String accountPhone;
	
	private String employeeType;
	private Timestamp employeeHtime; //고용일자
	private String employeeStatus; // 재직/휴직/퇴사
	
	private String accountStatus; // YN
}
