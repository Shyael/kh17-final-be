package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.vo.dashboard.DashboardAssignmentVO;
import com.kh.khedu.vo.dashboard.DashboardChildVO;
import com.kh.khedu.vo.dashboard.DashboardExamVO;

public interface AcademyDashboardDao {
    // 학생 - 미제출 과제 전체 개수
    int countPendingAssignments(int studentNo);

    // 학생 - 마감 가까운 미제출 과제 최대 5개
    List<DashboardAssignmentVO> selectPendingAssignments(int studentNo);

    // 학생 - 현재 응시 가능한 시험 개수
    int countAvailableExams(int studentNo);

    // 학생 - 예정 시험 개수
    int countUpcomingExams(int studentNo);

    // 학생 - 응시가능 + 예정 시험 최대 5개
    List<DashboardExamVO> selectDashboardExams(int studentNo);
    
    // 학부모 - 연결된 자녀 목록
    List<DashboardChildVO> selectChildren(int parentNo);
}