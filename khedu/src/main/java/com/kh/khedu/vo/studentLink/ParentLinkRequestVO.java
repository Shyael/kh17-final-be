package com.kh.khedu.vo.studentLink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name = "학생 연동 요청")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ParentLinkRequestVO {
	@NotBlank
	private String linkCode;
	private String relationship;
}
