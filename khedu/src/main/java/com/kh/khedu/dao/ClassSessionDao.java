package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.vo.classSession.AdminClassSessionStatusVO;
import com.kh.khedu.vo.classSession.CourseSessionVO;
import com.kh.khedu.vo.classSession.WeeklyClassSessionVO;

public interface ClassSessionDao {
	
	//scheduleNo와 날짜로 오늘실제수업 조회
	ClassSessionDto selectTodaySession(int scheduleNo, Timestamp sessionStart);
	
	//classSessionNo생성
	int sequence();
	//classSession 등록
	void insert(ClassSessionDto classSessionDto);
	
	//session넘버로 session조회
	ClassSessionDto selectOneBySessionNo(int sessionNo);

	//종료 시간(session_end)이 지났는데 여전히 '진행중'인 세션 일괄 종료 (스케쥴러용)
	boolean autoCloseExpiredSessions();
	
	//관리자용 classSession 등록
	void insertByAdmin(ClassSessionDto classSessionDto);

	//세션 상태를 '종료'로 업데이트
	boolean updateSessionToEnd(int sessionNo, String statusClosed);
	//관리자용 세션 상태 변경
	boolean updateStatusOnly(int sessionNo, String sessionStatus);
	//담당 강사 - 당일 세션 수정 시 사용
	boolean updateSessionByAdmin(AdminClassSessionStatusVO request);
	
	ClassSessionDto selectCurrentRunningSessionByStudent(int studentNo);
	
    //세션의 담당 강사의 사번을 조회  
    Integer selectInstructorNoBySessionNo(int sessionNo);
    
  	//만료된(종료시각이 지난) 진행중 세션 목록 조회
  	List<ClassSessionDto> selectExpiredRunningSessions();
  	//세션 상태 '종료'로 변경
  	boolean updateSessionStatusToClosed(int sessionNo);
  	
  	//강의 세션 조회
  	List<CourseSessionVO> selectSessionListByCourseNo(int courseNo);
  	
  	//금일 세션을 자동 생성을 위한 메소드
  	List<ScheduleDto> selectAutoStartTargets();
  	
  	// 기존 강의 충돌하는 개수 조회
	int checkClassroomConflict(AdminClassSessionStatusVO request);
	
	//실제로 생성된 이번주 수업 조회
	List<WeeklyClassSessionVO> selectWeeklySessions(
	        Timestamp weekStart,
	        Timestamp weekEnd
	);
	
}
