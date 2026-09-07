package com.kh.khedu.dao;

import com.kh.khedu.dto.ScheduleDto;

public interface ScheduleDao {
	
	//스케줄 등록
	int sequence();
	void insertSchedule(ScheduleDto scheduleDto);
}
