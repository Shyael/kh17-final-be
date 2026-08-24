package com.kh.khedu.vo.register;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "직원 등록 정보 객체")
@Data
public class EmployeeRegisterRequestVO {
	private String accountId; //이메일
	private String accountPassword;
	private String accountName;
	private String accountPhone;
	
	private String employeeType; //데스크/강사/원장
	private int roleNo;
}
