package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.vo.dashboard.DashboardAssignmentVO;
import com.kh.khedu.vo.dashboard.DashboardExamVO;

public interface EmployeeDashboardDao {
	
	//강사
	// 강사 - 진행 중 과제 전체 개수
    int countActiveAssignments(int employeeNo);

    // 강사 - 마감 임박 과제 최대 5개
    List<DashboardAssignmentVO> selectActiveAssignments(int employeeNo);

    // 강사 - 예정/응시 가능한 시험 전체 개수
    int countAvailableExams(int employeeNo);
    int countUpcomingExams(int employeeNo);

    // 강사 - 가까운 시험 최대 5개
    List<DashboardExamVO> selectDashboardExams(int employeeNo);
    
    //원장
    // 원장 / 데스크 - 전체 강사 수
    int countTutors();

    // 원장 / 데스크 - 진행 중 과제 전체 개수
    int countAllActiveAssignments();

    // 원장 / 데스크 - 진행 중 과제 최대 5개
    List<DashboardAssignmentVO> selectAllActiveAssignments();

    // 원장 / 데스크 - 다가오는 시험 전체 개수
    int countAllAvailableExams();
    int countAllUpcomingExams();

    // 원장 / 데스크 - 다가오는 시험 최대 5개
    List<DashboardExamVO> selectAllDashboardExams();
}
