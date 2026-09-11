package com.kh.khedu.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 보기 임시저장 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionOptionDraftVO {
    // 신규 보기면 null, 기존 보기면 값 존재
    private Integer optionNo;
    private String optionContent;
    private String optionIsAnswer;
    private int optionOrder;
}