package com.kh.khedu.dao;

import java.time.LocalDate;
import java.util.List;

import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

public interface ScheduleDao {
	
	//스케줄 번호생성
	int sequence();
	
	//스케줄 등록
	void insertSchedule(ScheduleDto scheduleDto);
	
	//강사 기존 수업과 충돌 확인
	public int checkTutorScheduleConflict(int employeeNo, ScheduleCreateRequestVO request);
	
	//강의실 기존 수업과 충돌 확인
	int checkClassroomScheduleConflict(int classroomNo, ScheduleCreateRequestVO request);
	
	//scheduleno로 스케줄정보 조회
	ScheduleDto selectOneByScheduleNo(int scheduleNo);
	
	//schedule_open의 날짜를 오늘로 변경
	boolean updateOpenByCourseNo(int scheduleNo, LocalDate today);

	List<ScheduleDto> selectActiveSchedules();
	//강좌넘버로 해당하는 schedule목록 불러오기
	List<ScheduleDto> selectListByCourseNo(int courseNo);
	
	// 개강일(schedule_open)이 도달했거나 null이 아닌 오늘 스케줄 조회
	List<ScheduleDto> selectTodayActiveSchedules(String todayKorean);

}
