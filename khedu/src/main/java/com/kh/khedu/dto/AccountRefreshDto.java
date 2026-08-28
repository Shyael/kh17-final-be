package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "회원 갱신토큰 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountRefreshDto {
	private String accountId;
	private String userAgent;
	private String userAddress;
	private String tokenValue;
}
