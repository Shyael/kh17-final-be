package com.kh.khedu.vo.assignment;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "과제 제출 목록 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentSubmitListVO {
	//목록쪽에서는 내용과 피드백 안보냄
    private int submitNo;
    private int assignmentNo;
    private String assignmentTitle;
    private int studentNo;
    private String accountName;
    private Timestamp submitWtime;
    private Timestamp submitEtime;
}