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
	boolean updateOpen(int scheduleNo, LocalDate today);

	List<ScheduleDto> selectActiveSchedules();
		
}
