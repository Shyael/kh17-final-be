package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="계정 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountDto {
	private int accountNo;
	private String accountId; //이메일
	private String accountPassword;
	private String accountName;
	private String accountPhone;
	private String accountStatus; //차단여부 Y N
	private Timestamp accountCtime;
	private Timestamp accountUtime;
	private String accountType; // (직원, 학생, 학부모)
}
