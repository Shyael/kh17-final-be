package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.dashboard.DashboardAssignmentVO;
import com.kh.khedu.vo.dashboard.DashboardCourseVO;
import com.kh.khedu.vo.dashboard.DashboardExamVO;

@Repository
public class EmployeeDashboardDaoMybatis implements EmployeeDashboardDao {

    @Autowired
    private SqlSession sqlSession;

    //강사
    @Override
    public int countActiveAssignments(int employeeNo) {
        return sqlSession.selectOne("mapper.employeeDashboard.countActiveAssignments", employeeNo);
    }

    @Override
    public List<DashboardAssignmentVO> selectActiveAssignments(int employeeNo) {
        return sqlSession.selectList("mapper.employeeDashboard.selectActiveAssignments", employeeNo);
    }
    
	@Override
	public int countAvailableExams(int employeeNo) {
		return sqlSession.selectOne("mapper.employeeDashboard.countAvailableExams", employeeNo);
	}

    @Override
    public int countUpcomingExams(int employeeNo) {
        return sqlSession.selectOne("mapper.employeeDashboard.countUpcomingExams", employeeNo);
    }

    @Override
    public List<DashboardExamVO> selectDashboardExams(int employeeNo) {
        return sqlSession.selectList("mapper.employeeDashboard.selectDashboardExams", employeeNo);
    }
    
    @Override
    public List<DashboardCourseVO> selectTutorDashboardTodayCourses(int employeeNo) {
    	return sqlSession.selectList("mapper.employeeDashboard.selectTutorDashboardTodayCourses", employeeNo);
    }
    
    @Override
    public int countTutorDashboardTodayCourses(int employeeNo) {
    	return sqlSession.selectOne("mapper.employeeDashboard.countTutorDashboardTodayCourses", employeeNo);
    }
    
    //원장/데스크
	@Override
	public int countTutors() {
		return sqlSession.selectOne("mapper.employeeDashboard.countTutors");
	}

	@Override
	public int countAllActiveAssignments() {
		 return sqlSession.selectOne("mapper.employeeDashboard.countAllActiveAssignments");
	}

	@Override
	public List<DashboardAssignmentVO> selectAllActiveAssignments() {
		 return sqlSession.selectList("mapper.employeeDashboard.selectAllActiveAssignments");
	}

	@Override
	public int countAllAvailableExams() {
		 return sqlSession.selectOne("mapper.employeeDashboard.countAllAvailableExams");
	}
	
	@Override
	public int countAllUpcomingExams() {
		 return sqlSession.selectOne("mapper.employeeDashboard.countAllUpcomingExams");
	}

	@Override
	public List<DashboardExamVO> selectAllDashboardExams() {
		 return sqlSession.selectList("mapper.employeeDashboard.selectAllDashboardExams");
	}
	
	@Override
	public List<DashboardCourseVO> selectAdminDashboardTodayCourses() {
		return sqlSession.selectList("mapper.employeeDashboard.selectAdminDashboardTodayCourses");
	}
	
	@Override
	public int countAdminDashboardTodayCourses() {
		return sqlSession.selectOne("mapper.employeeDashboard.countAdminDashboardTodayCourses");
	}
	
}