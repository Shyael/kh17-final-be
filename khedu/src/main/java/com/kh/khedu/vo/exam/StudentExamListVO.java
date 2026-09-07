package com.kh.khedu.vo.exam;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생용 시험 목록 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamListVO {
    // 시험 번호
    private int examNo;
    // 강의 번호
    private int courseNo;
    // 강의명
    private String courseTitle;
    // 시험 제목
    private String examTitle;
    // 시험 시작일
    private Timestamp examStart;
    // 시험 종료일
    private Timestamp examEnd;
    // 시험 제한시간
    // null이면 제한시간 없음
    private Integer examLimit;
    // 시험 상태
    private String examStatus;

    // ==================== 학생 응시정보 ====================
    // 응시 번호
    // 아직 응시하지 않았다면 null
    private Integer attemptNo;
    // 응시 상태
    // 미응시라면 null
    private String attemptStatus;
    // 제출 시간
    // 제출하지 않았다면 null
    private Timestamp attemptSubmit;
    // 시험 점수
    // 미응시/미제출이면 null
    private Integer attemptScore;
}