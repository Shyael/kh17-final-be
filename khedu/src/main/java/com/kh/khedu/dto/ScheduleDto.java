package com.kh.khedu.dto;


import java.time.LocalDate;
import java.time.LocalTime;

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
	private String scheduleWeek; //요일
	private LocalDate scheduleOpen; // 개강일 (null가능)
	private LocalDate scheduleClose; // 종강일 (null가능)
	private String scheduleStart; //시작 시간 (HH:mm)
	private String scheduleEnd; //종료 시간(HH:mm)
	private int classroomNo;
}
