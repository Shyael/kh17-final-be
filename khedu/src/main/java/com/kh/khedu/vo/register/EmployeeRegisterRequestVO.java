package com.kh.khedu.vo.register;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "직원 등록 정보 객체")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeRegisterRequestVO {
	private String accountId; //이메일
	private String accountPassword;
	private String accountName;
	private String accountPhone;
	private String accountType; // 직원, 학생, 학부모
	private String employeeType; //데스크/강사/원장
	private LocalDate employeeHtime;
	
	private List<Integer> roleNos; //권한
}
