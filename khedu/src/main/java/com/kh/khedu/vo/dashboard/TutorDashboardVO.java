package com.kh.khedu.vo.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorDashboardVO implements EmployeeDashboardVO {
	
	// 진행 중 과제 전체 개수
    private int activeAssignmentCount;

    // 마감 가까운 과제 최대 5개
    private List<DashboardAssignmentVO> activeAssignments;

    // 예정/응시 가능한 시험 전체 개수
    private int availableExamCount;
    private int upcomingExamCount;
    
    // 가까운 시험 최대 5개
    private List<DashboardExamVO> exams;
}
