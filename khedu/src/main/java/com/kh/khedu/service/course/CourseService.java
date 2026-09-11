package com.kh.khedu.service.course;

import java.util.List;

import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailResponseVO;
import com.kh.khedu.vo.course.CourseFormDataVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;
import com.kh.khedu.vo.course.CourseSimpleListVO;
import com.kh.khedu.vo.course.StudentCourseListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface CourseService {
	
	//강좌 등록 전 조회
	CourseFormDataVO getCourseFormData();
	
	//강의실 조회(강의 등록화면에 사용되는)
	List<ClassroomWhenRegisterVO> getAvailAbleClassrooms(AvailableClassroomRequestVO request);
	
	//강좌 등록
	void createCourse(TokenParseResponseVO parseVO, CourseCreateRequestVO request);
	
	//강좌 목록
	List<CourseListVO> getCourseList();
	
	//강좌 상세
	CourseDetailResponseVO getCourseDetail(int courseNo, TokenParseResponseVO parseVO);
	
	//강좌 검색 조회
	PageResponseVO<CourseListVO> selectList(CourseSearchVO search);
	
	//학생 본인 수강중인 강의목록
	List<StudentCourseListVO> selectListByStudent(int studentNo);
	
	//학부모용 학생 수강중인 강의목록
	List<StudentCourseListVO> selectListByParentStudent(
	        int parentNo,
	        int studentNo
	);
	//강사인지 다른 직원인지 체크
	List<CourseSimpleListVO> selectManageCourseList(
	        int employeeNo,
	        boolean tutor
	);

}
