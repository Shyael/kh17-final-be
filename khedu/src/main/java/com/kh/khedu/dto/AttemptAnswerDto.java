package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 답안 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttemptAnswerDto {
    private int attemptNo;
    private int questionNo;
    // 선택한 보기 번호
    // 미응답이면 null 가능
    private Integer optionNo;
    // 정답 여부 Y / N
    // 채점 전이면 null 가능
    private String isCorrect;
}