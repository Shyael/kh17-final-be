package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="실제 수업확인 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClassSessionDto {
	
	public static final String STATUS_RUNNING = "진행중";
	public static final String STATUS_CLOSED = "종료";
	public static final String STATUS_CANCEL = "취소";
	
	private int sessionNo;
	private int scheduleNo;
	private int classroomNo;
	private Timestamp sessionStart;
	private Timestamp sessionEnd;
	private String sessionStatus;
}
