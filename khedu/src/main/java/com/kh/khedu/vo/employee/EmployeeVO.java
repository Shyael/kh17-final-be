package com.kh.khedu.vo.employee;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="직원 등록 객체")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeVO {
	private int employeeNo;
	private int accountNo;
	private String employeeType;
	private Timestamp employeeHtime;
}
