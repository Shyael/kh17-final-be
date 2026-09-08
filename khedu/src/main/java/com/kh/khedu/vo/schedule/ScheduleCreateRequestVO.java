package com.kh.khedu.vo.schedule;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="수업 일정 등록 요청 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleCreateRequestVO {
	
	private LocalDate scheduleOpen; // 개강일 (null가능)
	
	private LocalDate scheduleClose; // 종강일 (null가능)
	
	@NotBlank(message = "수업 요일을 선택해주세요.")
	private String scheduleWeek; //요일
	
	@NotBlank(message = "수업 시작시간을 입력해주세요.")
	private String scheduleStart; //시작 시간 (HH:mm)
	@NotBlank(message = "수업 종료시간을 입력해주세요.")
	private String scheduleEnd; //종료 시간(HH:mm)
	
	private int classroomNo; //기본 강의실
}
