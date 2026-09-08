package com.kh.khedu.vo.tutor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "강사 조회 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class TutorSearchRequestVO {
	private Integer academySubjectNo;
	private String keyword;
}
