package com.kh.khedu.service;

import java.time.LocalDate;
import java.util.List;

import com.kh.khedu.vo.schedule.WeeklyTimetableVO;

public interface TimetableService {
	
	 List<WeeklyTimetableVO> getWeeklyTimetable(
	            int classroomNo,
	            LocalDate date
	    );
}
