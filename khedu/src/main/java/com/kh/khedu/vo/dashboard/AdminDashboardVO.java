package com.kh.khedu.vo.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardVO implements EmployeeDashboardVO {
    // 진행 중 과제
    private int activeAssignmentCount;
    private List<DashboardAssignmentVO> activeAssignments;
    
    // 다가오는 시험
    private int availableExamCount;
    private int upcomingExamCount;
    private List<DashboardExamVO> exams;
}
