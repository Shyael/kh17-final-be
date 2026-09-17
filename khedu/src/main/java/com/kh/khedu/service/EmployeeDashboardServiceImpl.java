package com.kh.khedu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.ConsultDao;
import com.kh.khedu.dao.EmployeeDashboardDao;
import com.kh.khedu.vo.dashboard.AdminDashboardVO;
import com.kh.khedu.vo.dashboard.DeskDashboardVO;
import com.kh.khedu.vo.dashboard.EmployeeDashboardVO;
import com.kh.khedu.vo.dashboard.TutorDashboardVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;


@Service
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {
	
	@Autowired
	EmployeeDashboardDao employeeDashboardDao;
	
	@Autowired
	private AdminDashboardService adminDashboardService;
	
	@Autowired
	private DeskDashboardService deskDashboardService;
	
	@Autowired
	private ConsultDao consultDao;
	 
	@Override
    public EmployeeDashboardVO getDashboard(
            TokenParseResponseVO parseVO) {
		
		int employeeNo = parseVO.getNoType();
		
		//원장
        if (parseVO.getRoleNames().contains("ADMIN")) {
        	 return AdminDashboardVO.builder()
	                     .activeAssignmentCount(employeeDashboardDao.countAllActiveAssignments())
	                     .activeAssignments(employeeDashboardDao.selectAllActiveAssignments())
	                     .availableExamCount(employeeDashboardDao.countAllAvailableExams())
	                     .upcomingExamCount(employeeDashboardDao.countAllUpcomingExams())
	                     .exams( employeeDashboardDao.selectAllDashboardExams())
	                     .courses(employeeDashboardDao.selectAdminDashboardTodayCourses())
	                     .todayCourses(employeeDashboardDao.countAdminDashboardTodayCourses())
	                     .pendingContractList(adminDashboardService.getPendingContractList())
	                     .contractExpiringList(adminDashboardService.getContractExpiringList())
	                     .payrollDueList(adminDashboardService.getPayrollDueList())
	                     .recentReservations(consultDao.selectRecentReservationList())
	                     .todayReservations(consultDao.selectTodayReservationList())
	                     .todayReservations(consultDao.selectTodayReservationList())
                     .build();
        }
        
        //데스크
        if (parseVO.getRoleNames().contains("DESK")) {
            return DeskDashboardVO.builder()
	                    .activeAssignmentCount(employeeDashboardDao.countAllActiveAssignments())
	                    .activeAssignments(employeeDashboardDao.selectAllActiveAssignments())
	                    .availableExamCount(employeeDashboardDao.countAllAvailableExams())
	                    .upcomingExamCount(employeeDashboardDao.countAllUpcomingExams())
	                    .exams( employeeDashboardDao.selectAllDashboardExams())
	                    .courses(employeeDashboardDao.selectAdminDashboardTodayCourses())
	                    .todayCourses(employeeDashboardDao.countAdminDashboardTodayCourses())
	                    .payrollDueList(deskDashboardService.getPayrollDueList())
	                    .recentReservations(consultDao.selectRecentReservationList())
	                    .todayReservations(consultDao.selectTodayReservationList())
	                    .todayReservations(consultDao.selectTodayReservationList())
                    .build();
        }

        //강사
        if (parseVO.getRoleNames().contains("TUTOR")) {
            return TutorDashboardVO.builder()
            			// 진행 중 과제 전체 개수
            			.activeAssignmentCount(employeeDashboardDao.countActiveAssignments(employeeNo))
            			// 진행 중 과제 최대 5개
                        .activeAssignments(employeeDashboardDao.selectActiveAssignments(employeeNo))
                        .availableExamCount(employeeDashboardDao.countAvailableExams(employeeNo))
                        .upcomingExamCount(employeeDashboardDao.countUpcomingExams(employeeNo))
                        .exams(employeeDashboardDao.selectDashboardExams(employeeNo))
                        .courses(employeeDashboardDao.selectTutorDashboardTodayCourses(employeeNo))
                        .todayCourses(employeeDashboardDao.countTutorDashboardTodayCourses(employeeNo))
                    .build();
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "대시보드 접근 권한이 없습니다."
        );
    }
}