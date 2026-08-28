package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="과제정보 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentDto {
	private int assignmentNo;
	private int courseNo;
	private int employeeNo;
	private String assignmentTitle;
	private String assignmentContent;
	private String assignmentStatus;
	private Timestamp assignmentDueDate;
	private Timestamp assignmentWtime;
}
