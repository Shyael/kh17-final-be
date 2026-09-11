package com.kh.khedu.vo.classSession;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "실제 수업 종료 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ClassSessionEndRequestVO {
	private int SessionNo; //class_session넘버
}
