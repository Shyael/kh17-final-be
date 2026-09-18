package com.kh.khedu.vo.attendance;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceStudentSearchResponseVO {
	private Integer courseNo;
	private String courseTitle;
	private Integer sessionNo;
	private LocalDateTime sessionStart;
	private LocalDateTime sessionEnd;
}
