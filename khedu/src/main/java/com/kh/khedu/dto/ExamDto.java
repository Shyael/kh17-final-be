package com.kh.khedu.dto;

import java.security.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamDto {
	private int examNo;
	private int courseNo;
	private int employeeNo;
	private String examTitle;
	private String examInfo;
	private Timestamp examStart;
	private Timestamp examEnd;
	//null이면 시간 제한 없음
	private Integer examLimit;
	private String examStatus;
	private Timestamp examWtime;
}
