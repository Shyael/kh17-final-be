package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;
import com.kh.khedu.vo.course.CourseSimpleListVO;
import com.kh.khedu.vo.course.StudentCourseListVO;

public interface CourseDao {
	List<CourseDto> selectTeachingListByEmployee(int employeeNo);
	
	//학생이 현재 수강 중인 강의 목록
	List<StudentCourseListVO> selectListByStudent(int studentNo);
	
	//강좌 번호 생성
	int sequence();
	
	// 강좌 등록
	void insertCourse(CourseDto courseDto);

	// 강좌 목록
	List<CourseListVO> selectCourseList();
	

	// 강좌 상세
	CourseDetailVO selectCourseDetail(int courseNo);
	
	// 강의 대상 학년 조회
    String selectCourseGrade(int courseNo);
    
    // 강의 현재 수강 인원 갱신
    void updateCourseCurrentCount(int courseNo);

	//강좌 검색 조회
	List<CourseListVO> selectSearchList(CourseSearchVO search);
	
	//검색 목록의 개수 조회
	int selectCount(CourseSearchVO search);
	
	//course_status를 모집중 -> 진행중으로 변경
	boolean updateStatus(int courseNo, String status);
	//courseNo로 강좌 조회
	CourseDto selectOneByCourseNo(int courseNo);
	//scheduleNo로 강좌 조회
	CourseDto selectOneByScheduleNo(int scheduleNo);
	
	//강사 사번(employee_no)으로 강사 이름 조회
	String selectTutorNameByEmployeeNo(int employeeNo);

	//관리 화면 강의 필터용
	// 원장/데스크 - 전체 강의 목록
	List<CourseSimpleListVO> selectManageCourseList();
	
	// 강사 - 본인이 담당하는 강의 목록
	List<CourseSimpleListVO> selectManageCourseListByEmployee(int employeeNo);
}
