package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="직원별 권한 저장 객체")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountRolesDto {
	private int accountNo;
	private int roleNo;
}
