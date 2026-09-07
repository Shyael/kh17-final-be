package com.kh.khedu.vo.exam;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사용 시험 응시자 목록 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamAttemptListVO {
    private Integer attemptNo;
    private int studentNo;
    // 학생 이름
    private String studentName;
    // 응시중 / 제출완료
    // 미응시면 null
    private String attemptStatus;
    // 응시 시작 시간
    private Timestamp attemptStart;
    // 제출 시간
    private Timestamp attemptSubmit;
    // 제출 전이면 null
    private Integer attemptScore;
}