package com.kh.khedu.dao;

import java.sql.Timestamp;

import com.kh.khedu.dto.ClassSessionDto;

public interface ClassSessionDao {
	
	//scheduleNo와 날짜로 오늘실제수업 조회
	ClassSessionDto selectTodaySession(int scheduleNo, Timestamp sessionStart);
	
	//classSessionNo생성
	int sequence();
	//classSession 등록
	void insert(ClassSessionDto classSessionDto);
	
	//session넘버로 session조회
	ClassSessionDto selectOneBySessionNo(int sessionNo);

	//session_stauts 상태변경(종료)
	boolean updateSessionStatus(int sessionNo, String statusClosed);
	
	//종료 시간(session_end)이 지났는데 여전히 '진행중'인 세션 일괄 종료 (스케쥴러용)
	int autoClosdedExpriedSessions();
	
	//관리자용 classSession 등록
	void insertByAdmin(ClassSessionDto classSessionDto);
		
	
	//관리자용 세션 상태 변경 (진행중, 종료, 취소)
	int updateStatusByAdmin(int sessionNo, String sessionStatus);

	ClassSessionDto selectCurrentRunningSessionByStudent(int studentNo);
}
