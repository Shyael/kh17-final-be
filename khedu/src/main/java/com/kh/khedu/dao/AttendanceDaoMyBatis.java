package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AttendanceDto;

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

}
