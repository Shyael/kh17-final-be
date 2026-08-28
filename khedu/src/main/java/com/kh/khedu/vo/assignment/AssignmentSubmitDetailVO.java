package com.kh.khedu.vo.assignment;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "과제 제출 상세정보 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentSubmitDetailVO {
    private int submitNo;
    private int assignmentNo;
    private String assignmentTitle;//과제 이름
    private int studentNo;
    private String accountName;//제출한 학생이름
    private String submitContent;//제출 내용
    private String submitComment;//피드백
    private Timestamp submitWtime;
    private Timestamp submitEtime;
}