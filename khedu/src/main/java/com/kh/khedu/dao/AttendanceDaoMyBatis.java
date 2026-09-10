package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AttendanceDto;
import com.kh.khedu.vo.attendance.AttendanceStudentResponseVO;

@Repository
public class AttendanceDaoMyBatis implements AttendanceDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public AttendanceDto selectBySessionAndStudent(int sessionNo, int studentNo) {
		Map<String, Object> param = new HashMap<>();
		param.put("sessionNo", sessionNo);
		param.put("studentNo", studentNo);
		return sqlSession.selectOne("mapper.attendance.selectBySessionAndStudent", param);
	}

	@Override
	public boolean updateAttendanceState(int attendanceNo, String attendanceState, Timestamp attendanceAt) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("attendanceNo", attendanceNo);
	    param.put("attendanceState", attendanceState); // XML의 #{attendanceState}와 일치
	    param.put("attendanceAt", attendanceAt);       // XML의 #{attendanceAt}와 일치
	    
	    return sqlSession.update("mapper.attendance.updateAttendanceState", param) > 0;
	}
//	세션 종료 시 미출결 수강생 일괄 결석 처리
	@Override
	public int updateAbsentForUncheckedStudents(int sessionNo) {
		return sqlSession.update("mapper.attendance.updateAbsentForUncheckedStudents", sessionNo);
	}

	@Override
	public void initAttendance(int sessionNo, int courseNo) {
		Map<String, Object> param = new HashMap<>();
	    param.put("sessionNo", sessionNo);
	    param.put("courseNo", courseNo);
		sqlSession.insert("mapper.attendance.initAttendance", param);
	}

	@Override
	public AttendanceDto selectOneByAttendanceNo(int attendanceNo) {
		return sqlSession.selectOne("mapper.attendance.selectOneByAttendanceNo", attendanceNo);
	}

	@Override
	public Integer selectTutorNoByAttendanceNo(int attendanceNo) {
		return sqlSession.selectOne("mapper.attendance.selectTutorNoByAttendanceNo", attendanceNo);
	}

	@Override
	public boolean updateAttendanceStateByAdmin(int attendanceNo, String attendanceState) {
		Map<String, Object> param = new HashMap<>();
	    param.put("attendanceNo", attendanceNo);
	    param.put("attendanceState", attendanceState);
		return sqlSession.update("mapper.attendance.updateAttendanceStateByAdmin", param) > 0;
	}
	//세션의 출석학생들 목록
	@Override
	public List<AttendanceStudentResponseVO> selectAttendanceListBySessionNo(int sessionNo) {
		return sqlSession.selectList("mapper.attendance.selectAttendanceListBySessionNo", sessionNo);
	}

}
