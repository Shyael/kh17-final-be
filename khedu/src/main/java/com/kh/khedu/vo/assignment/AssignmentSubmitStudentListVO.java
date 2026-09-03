package com.kh.khedu.vo.assignment;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "과제 학생 제출현황 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentSubmitStudentListVO {
    private int studentNo;
    private String accountName;
    // 미제출이면 null
    private Integer submitNo;
    private Timestamp submitWtime;
    private Timestamp submitEtime;
    private String submitComment;
}