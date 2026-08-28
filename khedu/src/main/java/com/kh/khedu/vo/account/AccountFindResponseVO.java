package com.kh.khedu.vo.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "계정 조회 응답용 데이터")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountFindResponseVO {
	private int accountNo;
	private String accountId; //이메일
	private String accountName;
	private String accountPhone;
	private String accountType; // (직원, 학생, 학부모)
}
