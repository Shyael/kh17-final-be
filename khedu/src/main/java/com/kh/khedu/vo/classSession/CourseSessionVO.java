package com.kh.khedu.vo.classSession;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="세션 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseSessionVO {
	private int sessionNo;
	private int scheduleNo;
	private int classroomNo;
	
	private Timestamp sessionStart;
	private Timestamp sessionEnd;
	private String sessionStatus;
	
	// 출결 요약 통계
	private int totalCount;
	private int presentCount;
	private int lateCount;
	private int absentCount;
}
