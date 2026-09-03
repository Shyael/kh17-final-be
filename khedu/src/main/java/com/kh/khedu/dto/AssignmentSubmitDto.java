package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "과제제출정보 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentSubmitDto {
	private int submitNo;
	private int assignmentNo;
	private int studentNo;
	private String submitContent;
	private String submitComment;
	private Timestamp submitWtime;
	private Timestamp submitEtime;
}
