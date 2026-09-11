package com.kh.khedu.vo.course;

import java.util.List;

import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.attendance.SessionAttendanceDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강의 상세페이지 통합 응답 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseDetailResponseVO {
	// [1] 강좌 및 스케줄 정보
	private CourseDto courseInfo; //강좌 기본 정보
	private String tutorName; //담당 강사 이름
	private List<ScheduleDto> scheduleList; //강좌 스케줄 목록
	private ClassSessionDto todaySession; //오늘 세션 정보 (시작 전이면 null)
	
	// [2] 출결 정보 (오늘 세션이 있으면 해당 세션 출석부, 없으면 null)
	private SessionAttendanceDetailVO attendanceDetail;
	
	// [3] 과제 정보
	private List<AssignmentListVO> assignmentList;
	// [4] 시험 정보
	private List<ExamListVO> examList;
	
	// [5] 학생 정보
	private List<CourseStudentListVO> studentList;
	
}
