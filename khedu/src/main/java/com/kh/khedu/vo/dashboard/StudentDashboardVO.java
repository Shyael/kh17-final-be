package com.kh.khedu.vo.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentDashboardVO implements AcademyDashboardVO {
	// 아직 제출하지 않은 진행 중 과제
    private int pendingAssignmentCount;

    // 마감 가까운 미제출 과제 최대 5개
    private List<DashboardAssignmentVO> pendingAssignments;

    // 현재 바로 응시 가능한 시험
    private int availableExamCount;

    // 아직 시작하지 않은 예정 시험
    private int upcomingExamCount;

    // 응시가능 + 예정 시험 최대 5개
    private List<DashboardExamVO> exams;
    
    // 학생 - 본인의 금일 강좌 조회
    private int todayCourses;
    
    // 학생 - 금일 수업 목록
    private List<DashboardCourseVO> courses;
}
