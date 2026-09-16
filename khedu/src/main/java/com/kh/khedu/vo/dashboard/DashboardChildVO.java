package com.kh.khedu.vo.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardChildVO {
    private int studentNo;
    private String studentName;

    // 미제출 과제
    private int pendingAssignmentCount;
    private List<DashboardAssignmentVO> pendingAssignments;

    // 현재 진행 중인 시험
    private int availableExamCount;

    // 예정 시험
    private int upcomingExamCount;

    // 진행 중 + 예정 시험 최대 5개
    private List<DashboardExamVO> exams;
    
    // 금일 수업 개수
    private int todayCourses;
    
    // 금일 수업 목록
    private List<DashboardCourseVO> courses;
}