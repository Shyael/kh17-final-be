package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="직원 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeDto {
	private int employeeNo;
	private int accountNo;
	private String employeeType;
	private Timestamp employeeHtime; //고용일자
	private String employeeStatus; // 재직/휴가/퇴사
}
