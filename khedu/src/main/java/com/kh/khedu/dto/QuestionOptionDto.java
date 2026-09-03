package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 문제 보기 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionOptionDto {
    private int optionNo;
    private int questionNo;
    private String optionContent;
    // 정답 여부 Y / N
    private String optionIsAnswer;
    // 보기 순서
    private int optionOrder;
}