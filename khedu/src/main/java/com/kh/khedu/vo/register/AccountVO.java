package com.kh.khedu.vo.register;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Schema(name="계정 등록(회원가입/회원등록) 정보 객체")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountVO {
	private int accountNo;
	private String accountId; //이메일
	private String accountPassword;
	private String accountName;
	private String accountPhone;
}
