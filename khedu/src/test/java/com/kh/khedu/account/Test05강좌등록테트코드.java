package com.kh.khedu.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.khedu.service.course.CourseService;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootTest
public class Test05강좌등록테트코드 {
	@Autowired
	private CourseService courseService;
	@Test
	void createCourse_50개_등록() {

	    for (int i = 1; i <= 50; i++) {

	        CourseCreateRequestVO request =
	                new CourseCreateRequestVO();

	        request.setEmployeeNo(7);
	        request.setAcademySubjectNo(4);
	        request.setGradeNo((i % 3) + 1);
	        
	        request.setCourseTitle("테스트 강좌 " + i);
	        request.setCourseSubject("수학");

	        request.setCourseLimit(20);
	        request.setCourseFee(300000);
	        request.setCourseInfo("페이지네이션 테스트용 강좌 " + i);
	        request.setCourseType("정규");


	        ScheduleCreateRequestVO schedule =
	                new ScheduleCreateRequestVO();

	        schedule.setScheduleOpen(
	                LocalDate.of(2026, 9, 15)
	        );

	        schedule.setScheduleClose(
	                LocalDate.of(2026, 12, 31)
	        );

	        // 월~금으로 분산
	        String[] weeks = {
	            "월", "화", "수", "목", "금"
	        };

	        schedule.setScheduleWeek(
	                weeks[(i - 1) % 5]
	        );

	        // 시간도 다르게
	        int hour = 9 + ((i - 1) % 5) * 2;

	        schedule.setScheduleStart(
	        	    String.format("%02d:00", hour)
	        	);

	        	schedule.setScheduleEnd(
	        	    String.format("%02d:30", hour + 1)
	        	);

	        	schedule.setClassroomNo(1);

	        	request.setSchedules(
	        	    List.of(schedule)
	        	);

	        // 등록
	        assertDoesNotThrow(() ->
	                courseService.createCourse(
	                        null,
	                        request
	                )
	        );
	    }
	}
}
