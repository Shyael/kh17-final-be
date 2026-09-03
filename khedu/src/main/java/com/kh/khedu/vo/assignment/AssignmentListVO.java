package com.kh.khedu.vo.assignment;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "과제 목록 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentListVO {
	//과제 상세랑 동일한데 내용만 빠짐
    private int assignmentNo;
    private int courseNo;
    private String courseTitle;
    private int employeeNo;
    private String accountName;
    private String assignmentTitle;
    private String assignmentStatus;
    private Timestamp assignmentDueDate;
    private Timestamp assignmentWtime;
}