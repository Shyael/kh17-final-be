package com.kh.khedu.vo.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name="비밀번호 변경요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordRequestVO {
	@NotNull
	private String prevAccountPassword;
	@NotNull
	private String newAccountPassword;
}
