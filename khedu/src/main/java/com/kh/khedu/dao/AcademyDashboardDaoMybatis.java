package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.dashboard.DashboardAssignmentVO;
import com.kh.khedu.vo.dashboard.DashboardChildVO;
import com.kh.khedu.vo.dashboard.DashboardCourseVO;
import com.kh.khedu.vo.dashboard.DashboardExamVO;

@Repository
public class AcademyDashboardDaoMybatis implements AcademyDashboardDao {

    @Autowired
    private SqlSession sqlSession;


    @Override
    public int countPendingAssignments(int studentNo) {
        return sqlSession.selectOne("mapper.academyDashboard.countPendingAssignments", studentNo);
    }

    @Override
    public List<DashboardAssignmentVO> selectPendingAssignments(int studentNo) {
        return sqlSession.selectList("mapper.academyDashboard.selectPendingAssignments", studentNo);
    }

    @Override
    public int countAvailableExams(int studentNo) {
        return sqlSession.selectOne("mapper.academyDashboard.countAvailableExams", studentNo);
    }

    @Override
    public int countUpcomingExams(int studentNo) {
        return sqlSession.selectOne("mapper.academyDashboard.countUpcomingExams", studentNo);
    }

    @Override
    public List<DashboardExamVO> selectDashboardExams(int studentNo) {
        return sqlSession.selectList("mapper.academyDashboard.selectDashboardExams", studentNo);
    }
    
    @Override
    public List<DashboardChildVO> selectChildren(int parentNo) {
        return sqlSession.selectList("mapper.academyDashboard.selectChildren", parentNo);
    }
    
    // 학생 - 금일 수업 개수
	@Override
	public int countStudentDashboardTodayCourses(int noType) {
		return sqlSession.selectOne("mapper.academyDashboard.countStudentDashboardTodayCourses", noType);
	}
	
	// 학생 - 금일 수업 목록
	@Override
	public List<DashboardCourseVO> selectStudentDashboardTodayCourses(int noType) {
		return sqlSession.selectList("mapper.academyDashboard.selectStudentDashboardTodayCourses", noType);
	}
}