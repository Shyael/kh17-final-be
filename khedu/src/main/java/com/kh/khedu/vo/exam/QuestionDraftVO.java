package com.kh.khedu.vo.exam;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 문제 임시저장 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionDraftVO {
    // 신규 문제면 null, 기존 문제면 값 존재
    private Integer questionNo;
    private String questionContent;
    private int questionScore;
    private String questionComment;
    private int questionOrder;
    private List<QuestionOptionDraftVO> optionList;
}