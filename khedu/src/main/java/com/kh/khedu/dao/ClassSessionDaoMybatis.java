package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ClassSessionDto;

@Repository
public class ClassSessionDaoMybatis implements ClassSessionDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public ClassSessionDto selectTodaySession(int scheduleNo, Timestamp sessionStart) {
		Map<String, Object> param = new HashMap<>();
		param.put("scheduleNo", scheduleNo);
		param.put("sessionStart", sessionStart);
		return sqlSession.selectOne("mapper.classSession.selectTodaySession", param);
	}
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.classSession.sequence");
	}
	
	@Override
	public void insert(ClassSessionDto classSessionDto) {
		sqlSession.insert("mapper.classSession.insert", classSessionDto);
	}
	
	@Override
	public ClassSessionDto selectOneBySessionNo(int sessionNo) {
		return sqlSession.selectOne("mapper.classSession.selectOneBySessionNo", sessionNo);
	}
	
	@Override
	public boolean autoCloseExpiredSessions() {
		return sqlSession.update("mapper.classSession.autoCloseExpiredSessions") > 0;
	}
	
	@Override
	public void insertByAdmin(ClassSessionDto classSessionDto) {
		sqlSession.insert("mapper.classSession.insertByAdmin", classSessionDto);
	}

	// 세션 종료 처리 (상태 + 종료시간)
	@Override
	public boolean updateSessionToEnd(int sessionNo, String sessionStatus) {
		Map<String, Object> param = new HashMap<>();
		param.put("sessionNo", sessionNo);
		param.put("sessionStatus", sessionStatus);
		return sqlSession.update("mapper.classSession.updateSessionToEnd", param) > 0;
	}

	// [추가] 관리자 단순 상태 변경
	@Override
	public boolean updateStatusOnly(int sessionNo, String sessionStatus) {
		Map<String, Object> param = new HashMap<>();
		param.put("sessionNo", sessionNo);
		param.put("sessionStatus", sessionStatus);
		return sqlSession.update("mapper.classSession.updateStatusOnly", param) > 0;
	}

	@Override
	public ClassSessionDto selectCurrentRunningSessionByStudent(int studentNo) {
		return sqlSession.selectOne("mapper.classSession.selectCurrentRunningSessionByStudent", studentNo);
	}

	@Override
	public Integer selectInstructorNoBySessionNo(int sessionNo) {
		return sqlSession.selectOne("mapper.classSession.selectInstructorNoBySessionNo", sessionNo);
	}

	@Override
	public List<ClassSessionDto> selectExpiredRunningSessions() {
		return sqlSession.selectList("mapper.classSession.selectExpiredRunningSessions");
	}

	@Override
	public boolean updateSessionStatusToClosed(int sessionNo) {
		return sqlSession.update("mapper.classSession.updateSessionStatusToClosed", sessionNo) > 0;
	}
	
}