package com.kh.khedu.vo.exam;

import java.sql.Timestamp;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 결과 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamResultVO {
    private int attemptNo;
    private int examNo;
    private int courseNo;
    private String courseTitle;
    private String examTitle;
    // 학생 획득 점수
    private int attemptScore;
    // 시험 총점
    private int totalScore;
    private Timestamp attemptSubmit;
    // 문제별 결과
    private List<QuestionResultVO> questionList;
    //학생 번호
    private int studentNo;
    //학생 이름
    private String studentName;
}