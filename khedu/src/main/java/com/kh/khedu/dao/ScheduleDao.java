package com.kh.khedu.dao;

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
		
}
