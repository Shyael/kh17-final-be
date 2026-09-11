package com.kh.khedu.vo.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name="아이디 찾기 요청")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class FindAccountIdRequestVO {
	@NotBlank
	private String accountName;
	@NotBlank
	private String accountPhone;
}
