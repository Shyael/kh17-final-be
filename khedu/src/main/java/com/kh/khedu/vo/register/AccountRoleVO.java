package com.kh.khedu.vo.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountRoleVO {
	private int accountNo;
	private int roleNo;
}
