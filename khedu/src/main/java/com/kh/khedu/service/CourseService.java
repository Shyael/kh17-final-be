package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface CourseService {
	
	//강좌 등록
	void createCourse(TokenParseResponseVO parseVO, CourseCreateRequestVO request);
	
	//강좌 목록
	List<CourseListVO> getCourseList();
	
	//강좌 상세
	CourseDetailVO getCourseDetail(int courseNo);
}
