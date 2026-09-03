package com.kh.khedu.vo.assignment;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생 과제 목록 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignmentListVO {
    // 과제 정보
    private int assignmentNo;
    private int courseNo;
    private String courseTitle;
    // 담당 강사명
    private String accountName;
    
    private String assignmentTitle;
    private String assignmentStatus;
    private Timestamp assignmentDueDate;
    private Timestamp assignmentWtime;

    // 제출 정보
    // 미제출일 경우 null
    private Integer submitNo;
    // 피드백 여부 확인
    private String submitComment;
    private Timestamp submitWtime;
    private Timestamp submitEtime;
}