package com.kh.khedu.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 결과 보기 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionResultOptionVO {
    private int optionNo;
    private String optionContent;
    private int optionOrder;
    // 학생이 선택한 보기인지
    private boolean selected;
    // 실제 정답인지
    private boolean correct;
}