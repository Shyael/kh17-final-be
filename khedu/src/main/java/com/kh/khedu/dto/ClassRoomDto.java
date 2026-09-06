package com.kh.khedu.dto;

import java.util.List;

import com.kh.khedu.vo.course.CourseVO;
import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강의실 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClassRoomDto {
	private int classroomNo;
	private String classroomName;
	private int classroomCapacity; //수용인원 0초과
	private String classroomStatus; //기본 사용가능, / 사용중/사용가능/사용불가능/기타
}
