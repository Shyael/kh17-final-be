package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ClassSessionDto;

@Repository
public class ClassSessionDaoMybatis implements ClassSessionDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	//ScheduleNo로 classSession내용 찾기
	@Override
	public ClassSessionDto selectTodaySession(int scheduleNo, Timestamp sessionStart) {
		Map<String, Object> param = new HashMap<>();
		param.put("scheduleNo", scheduleNo);
		param.put("sessionStart", sessionStart);
		return sqlSession.selectOne("mapper.classSession.selectTodaySession", param);
	}
	
	//classSessionNo생성
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.classSession.sequence");
	}
	
	//classSession 등록
	@Override
	public void insert(ClassSessionDto classSessionDto) {
		sqlSession.insert("mapper.classSession.insert", classSessionDto);
	}
	
	//session넘버로 session조회
	@Override
	public ClassSessionDto selectOneBySessionNo(int sessionNo) {
		return sqlSession.selectOne("mapper.classSession.selectOneBySessionNo", sessionNo);
	}
	
	//세션 상태 종료로 변경
	@Override
	public boolean updateSessionStatus(int sessionNo, String statusClosed) {
		Map<String, Object> param = new HashMap<>();
		param.put("sessionNo", sessionNo);
		param.put("statusClosed", statusClosed);
		return sqlSession.update("mapper.classSession.updateSessionStatus") > 0;
	}
	
	//수업이 만료되었는데, 진행중 상태를 스케줄러로 자동 종료로 전환
	@Override
	public int autoClosdedExpriedSessions() {
		return sqlSession.update("mapper.classSession.autoClosdedExpriedSessions");
	}
	
	//관리자용 등록
	@Override
	public void insertByAdmin(ClassSessionDto classSessionDto) {
		sqlSession.insert("mapper.classSession.insertByAdmin", classSessionDto);
	}
	//관리자용 세션 제어
	@Override
	public int updateStatusByAdmin(int sessionNo, String sessionStatus) {
		Map<String, Object> param = new HashMap<>();
		param.put("sessionNo", sessionNo);
		param.put("sessionStatus", sessionStatus);
		return sqlSession.update("mapper.classSession.updateStatusByAdmin", param);
	}
	// 학생 출결관리
	@Override
	public ClassSessionDto selectCurrentRunningSessionByStudent(int studentNo) {
		return sqlSession.selectOne("mapper.classSession.selectCurrentRunningSessionByStudent", studentNo);
	}


}
