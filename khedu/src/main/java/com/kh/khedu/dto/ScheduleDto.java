package com.kh.khedu.dto;


import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "강의 시간표 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScheduleDto {
	private int scheduleNo;
	private int courseNo;
	private Timestamp scheduleOpen; //개강일
	private Timestamp scheduleClose; //종강일
	private String scheduleWeek; //요일
	private Timestamp scheduleStart;
	private Timestamp scheduleEnd;
	private int classroomNo;
}
