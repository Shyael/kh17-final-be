package com.kh.khedu.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생용 시험 보기 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentQuestionOptionVO {
    private int optionNo;
    private String optionContent;
    private int optionOrder;
}