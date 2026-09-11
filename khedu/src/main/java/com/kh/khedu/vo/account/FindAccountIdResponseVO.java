package com.kh.khedu.vo.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class FindAccountIdResponseVO {
	private boolean result;
	private String message;
	private String accountId;
}
