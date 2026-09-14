package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AcademyDashboardDao;
import com.kh.khedu.vo.dashboard.AcademyDashboardVO;
import com.kh.khedu.vo.dashboard.DashboardChildVO;
import com.kh.khedu.vo.dashboard.ParentDashboardVO;
import com.kh.khedu.vo.dashboard.StudentDashboardVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public class AcademyDashboardServiceImpl implements AcademyDashboardService {

    @Autowired
    AcademyDashboardDao academyDashboardDao;

    @Override
    public AcademyDashboardVO getDashboard(TokenParseResponseVO parseVO) {

        int noType = parseVO.getNoType();

        // 학생
        if (parseVO.getRoleNames().contains("STUDENT")) {

            return StudentDashboardVO.builder()
	                    .pendingAssignmentCount(academyDashboardDao.countPendingAssignments(noType))
	                    .pendingAssignments( academyDashboardDao.selectPendingAssignments(noType))
	                    .availableExamCount(academyDashboardDao.countAvailableExams(noType))
	                    .upcomingExamCount(academyDashboardDao.countUpcomingExams(noType))
	                    .exams(academyDashboardDao.selectDashboardExams(noType))
                    .build();
        }

        // 학부모
        if (parseVO.getRoleNames().contains("PARENT")) {
            int parentNo = noType;

            List<DashboardChildVO> children = academyDashboardDao.selectChildren(parentNo);

            for (DashboardChildVO child : children) {
                int studentNo = child.getStudentNo();

                child.setPendingAssignmentCount(
                        academyDashboardDao
                                .countPendingAssignments(studentNo)
                );
                child.setPendingAssignments(
                        academyDashboardDao
                                .selectPendingAssignments(studentNo)
                );
                child.setAvailableExamCount(
                        academyDashboardDao
                                .countAvailableExams(studentNo)
                );
                child.setUpcomingExamCount(
                        academyDashboardDao
                                .countUpcomingExams(studentNo)
                );
                child.setExams(
                        academyDashboardDao
                                .selectDashboardExams(studentNo)
                );
            }

            // 모든 자녀를 처리한 뒤 반환
            return ParentDashboardVO.builder()
                    .children(children)
                    .build();
        }

        // 학생도 학부모도 아닌 경우
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "대시보드 접근 권한이 없습니다."
        );
    }
}