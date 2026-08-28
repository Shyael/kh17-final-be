package com.kh.khedu.vo.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="비밀번호 변경응답 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordResponseVO {
	private boolean result; //true 성공, false: 실패
	private String message; //상태메세지
}
