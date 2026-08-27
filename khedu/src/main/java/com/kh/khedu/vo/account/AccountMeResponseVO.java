package com.kh.khedu.vo.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountMeResponseVO {
	private int accountNo;
	private String accountId; //이메일
	private String accountName;
	private String accountPhone;
	private String accountType; // (직원, 학생, 학부모)
}
