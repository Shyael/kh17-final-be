package com.kh.khedu.vo.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class CheckPasswordRequestVO {
	@NotBlank
	private String accountPassword;
}
