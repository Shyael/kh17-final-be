package com.kh.khedu.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardCourseVO {
	private Integer courseNo;  //강의 번호
	private String courseSubject; //과목명
	private String courseTitle; //강의명
	private String gradeLevel; //대상 학년
	private String tutorName; // 담당 강사명
	private String scheduleInfo; // 수업 시간 요일
	private String classroomInfo; // 강의실
}
