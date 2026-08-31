package com.kh.khedu.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name="로그인 요청 API")

@Data
public class AuthLoginRequestVO {
	private String accountId;
	private String accountPassword;
	private String loginType;
}
