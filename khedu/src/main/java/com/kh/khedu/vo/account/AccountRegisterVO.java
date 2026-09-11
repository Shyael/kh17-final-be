package com.kh.khedu.vo.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Schema(name="계정 등록(회원가입/회원등록) 정보 객체")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountRegisterVO {
	private int accountNo;
	private String accountId; //이메일
	private String accountPassword;
	private String accountName;
	private String accountPhone;
	private String accountType; //직원/학생/학부모
	private String accountBirth;
	private String accountStatus;
}
