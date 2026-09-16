package com.kh.khedu.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.TimetableService;
import com.kh.khedu.vo.schedule.WeeklyTimetableVO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "강의 주간 시간표")
@RestController
@RequestMapping("/api/employee/timetable")
public class TimetableRestController {

	@Autowired
	private TimetableService timetableService;
	
	//강의실별 주간 시간표 조회
	@GetMapping
	public List<WeeklyTimetableVO> weeklyTimetable(
			@RequestParam int classroomNo,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate date
	){
		//날짜를 보내지 않음녀 오늘이 포함된 주 조회
		if(date == null) {
			date = LocalDate.now();
		}
		
		return timetableService.getWeeklyTimetable(classroomNo, date);
		
	}
}
