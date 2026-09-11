package com.kh.khedu.vo.classroom;

import java.util.List;

import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강의 등록시 사용가능 강의실 응답 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AvailableClassroomRequestVO {
	private int courseLimit;
	private List<ScheduleCreateRequestVO> schedules;
}
