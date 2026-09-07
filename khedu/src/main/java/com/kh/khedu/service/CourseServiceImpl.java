package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.error.WhoAreYouException;
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
		if(parseVO.getAccountType() != AccountType.EMPLOYEE.getDescription()) {
			throw new WhoAreYouException();
		}
		
		//[1] 강의 정보 등록
		CourseDto courseDto = new CourseDto();
		BeanUtils.copyProperties(request, courseDto);
		int courseNo = courseDao.sequence();
		
		courseDto.setCourseNo(courseNo);
		
		courseDao.insertCourse(courseDto);
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
