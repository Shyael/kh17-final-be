package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseDao courseDao;
	
	//강좌 등록
	@Override
	public void createCourse(
			TokenParseResponseVO parseVO,
			CourseCreateRequestVO request) {
		
		//(+추가) 해당 권한자 인지 조회 : 강사, 데스크, 원장
		
		int courseNo = courseDao.sequence();
		request.setCourseNo(courseNo);
		courseDao.insertCourse(request);
	}

	//강좌 목록
	@Override
	public List<CourseListVO> getCourseList() {
		return courseDao.selectCourseList();
	}
	
	
	//강좌 상세
	@Override
	public CourseDetailVO getCourseDetail(int courseNo) {
		return courseDao.selectCourseDetail(courseNo);
	}

}
