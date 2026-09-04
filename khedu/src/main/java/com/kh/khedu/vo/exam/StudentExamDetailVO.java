package com.kh.khedu.vo.exam;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생용 시험 상세 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamDetailVO {
    // 시험 정보
    private int examNo;
    private int courseNo;
    private String courseTitle;
    private String examTitle;
    private String examInfo;
    private Timestamp examStart;
    private Timestamp examEnd;
    // 제한시간(분)
    // null이면 제한시간 없음
    private Integer examLimit;
    private String examStatus;
    // 시험 구성 정보
    private int questionCount;
    private int totalScore;
    // ==================== 학생 응시정보 ====================
    // 미응시라면 null
    private Integer attemptNo;
    // 응시중 / 제출완료
    // 미응시라면 null
    private String attemptStatus;
    // 실제 시험 시작 시간
    private Timestamp attemptStart;
    // 제출 시간
    private Timestamp attemptSubmit;
    // 제출 전이면 null
    private Integer attemptScore;
}