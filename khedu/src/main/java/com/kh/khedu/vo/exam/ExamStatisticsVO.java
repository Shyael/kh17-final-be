package com.kh.khedu.vo.exam;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 통계 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamStatisticsVO {
    //전체 수강생 수
    private int totalStudentCount;
    //제출 완료 인원
    private int submittedCount;
    //응시중 인원
    private int inProgressCount;
    //미응시 인원
    private int notAttemptedCount;
    //평균 점수
    private Double averageScore;
    //최고 점수
    private Integer highestScore;
    //최저 점수
    private Integer lowestScore;
    //문항별 통계
    private List<QuestionStatisticsVO> questionStatistics;
}