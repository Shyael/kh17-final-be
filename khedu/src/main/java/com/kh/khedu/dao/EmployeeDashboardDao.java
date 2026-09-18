package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.vo.dashboard.DashboardAssignmentVO;
import com.kh.khedu.vo.dashboard.DashboardCourseVO;
import com.kh.khedu.vo.dashboard.DashboardExamVO;
import com.kh.khedu.vo.dashboard.DashboardPaymentVO;

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
    
    // 강사 - 금일 본인 강좌 조회 
    List<DashboardCourseVO> selectTutorDashboardTodayCourses(int employeeNo);
    
    // 강사 - 금일 모든 강좌 개수
    int countTutorDashboardTodayCourses(int employeeNo);
    
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
    
    // 원장/데스크 - 금일 모든 강좌 조회(상위 5개만)
    List<DashboardCourseVO> selectAdminDashboardTodayCourses();
    
    //원장/데스크 - 금일 모든 강좌 개수
	int countAdminDashboardTodayCourses();
	
	//원장 / 데스크 - 미납 정보(이번달 제외 가장 오래된 것 부터)
	List<DashboardPaymentVO> selectLongTermUnpaidPayments();
    
}
