package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "문제 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionDto {
	private int questionNo;
	private int examNo;
	private String questionContent;
	private int questionScore;
	private String questionComment;//문제 해설
	private int questionOrder;//문제 표시 순서 
}
