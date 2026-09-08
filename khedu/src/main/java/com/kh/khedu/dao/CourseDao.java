package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;

public interface CourseDao {
	List<CourseDto> selectTeachingListByEmployee(int employeeNo);
	
	//강좌 번호 생성
	int sequence();
	
	// 강좌 등록
	void insertCourse(CourseDto courseDto);

	// 강좌 목록
	List<CourseListVO> selectCourseList();

	// 강좌 상세
	CourseDetailVO selectCourseDetail(int courseNo);
	
}
