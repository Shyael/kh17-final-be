package com.kh.khedu.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 문항별 통계 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatisticsVO {
    private int questionNo;
    private int questionOrder;
    private String questionContent;
    //이 문제를 맞힌 학생 수
    private int correctCount;
    //제출한 학생 수
    private int submittedCount;
    //정답률
    private double correctRate;
}