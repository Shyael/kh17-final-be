package com.kh.khedu.vo.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DeskDashboardVO implements EmployeeDashboardVO {
    // 진행 중 과제
    private int activeAssignmentCount;
    private List<DashboardAssignmentVO> activeAssignments;
    
    // 다가오는 시험
    private int availableExamCount;
    private int upcomingExamCount;
    private List<DashboardExamVO> exams;
    
    // 데스크 - 금일 상위 5개의 강의만 보여줌
    private int todayCourses; // 금일 강좌 개수
    private List<DashboardCourseVO> courses;
}
