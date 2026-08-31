package com.kh.khedu.vo.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name="비밀번호 찾기 응답")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class FindAccountPasswordRequestVO {
	@NotBlank
	private String accountId;
	@NotBlank
	private String accountName;
	@NotBlank
	private String accountPhone;
}
