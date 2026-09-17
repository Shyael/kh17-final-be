package com.kh.khedu.vo.dashboard;

import java.util.List;

import com.kh.khedu.vo.payroll.request.DashboardContractExpiringVO;
import com.kh.khedu.vo.payroll.request.DashboardPendingContractVO;
import com.kh.khedu.vo.payroll.response.DashboardPayrollDueVO;

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
    
    // 금일 강좌 목록
    private int todayCourses; // 금일 강좌 개수
    private List<DashboardCourseVO> courses;
    
    //대기 계약, 다가오는 계약 만료, 급여일..
    private List<DashboardPendingContractVO> pendingContractList;
    private List<DashboardContractExpiringVO> contractExpiringList;
    private List<DashboardPayrollDueVO> payrollDueList;
    
    private int pendingContractCount;
    private int contractExpiringCount;
    private int payrollDueCount;
    
    //오늘상담예약, 신규 상담예약 
    private List<DashboardReservationVO> recentReservations;
    private List<DashboardReservationVO> todayReservations;
    private int todayReservationCount;
}
