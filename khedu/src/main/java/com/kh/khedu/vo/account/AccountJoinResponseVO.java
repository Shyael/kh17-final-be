package com.kh.khedu.vo.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "회원가입 응답")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountJoinResponseVO {
	private int accountNo;
	private int targetNo;
	private String accountId;
	private String accountName;
	private String accountType; //회원 유형 학생/직원/학부모
	private String message;//안내 메세지
}
