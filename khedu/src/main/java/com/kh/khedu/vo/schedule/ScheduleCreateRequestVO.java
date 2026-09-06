package com.kh.khedu.vo.schedule;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학생 수업 스케줄 응답 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleCreateRequestVO {
	private String scheduleWeek; //요일
	private Timestamp scheduleOpen; // 개강일 (null가능)
	private Timestamp scheduleClose; // 종강일 (null가능)
	private Timestamp scheduleStart; //시작 시간 (HH:mm)
	private Timestamp scheduleEnd; //종료 시간(HH:mm)
	private int classroomNo; //기본 강의실
}
