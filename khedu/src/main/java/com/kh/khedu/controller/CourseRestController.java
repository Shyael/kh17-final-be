package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.course.CourseService;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseFormDataVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;
import com.kh.khedu.vo.course.CourseSimpleListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "강좌 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee/course")
public class CourseRestController {
	
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private CourseService courseService;
	
	@ApiResponse(responseCode = "200", description = "강좌 등록 초기 데이터 조회")
	@GetMapping("/form-data")
	public CourseFormDataVO getFormData() {

	    return courseService.getCourseFormData();
	}
	
	// 강좌 등록
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping("/")
	public ResponseEntity<Void> courseInsert(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody CourseCreateRequestVO request) {
		courseService.createCourse(parseVO, request);
		return ResponseEntity.ok().build();
	}
	
	//사용가능 강의실 판단
	@ApiResponse(responseCode = "200", description = "사용 가능")
	@PostMapping("/available-classrooms")
	public List<ClassroomWhenRegisterVO> getAvailableClassrooms(
			@RequestBody AvailableClassroomRequestVO request){
		return courseService.getAvailAbleClassrooms(request);
	}
	
	
	// 강좌 조회
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/list")
	public PageResponseVO<CourseListVO> courseList(
			@Valid @ModelAttribute CourseSearchVO search
	){
		return courseService.selectList(search);
	}
	
	
	// 로그인한 강사의 진행중인 강의 목록 조회
	@Operation(summary = "내가 수업중인 강의 목록 조회")
	@GetMapping("/tutor")
	public List<CourseDto> selectListByEmployee(
	        @CurrentUser TokenParseResponseVO parseVO) {
	    return courseDao.selectTeachingListByEmployee(
	            parseVO.getNoType());
	}
	
	@Operation(summary = "관리용 강의 목록 조회")
	@GetMapping("/manage")
	public List<CourseSimpleListVO> selectManageCourseList(
	        @CurrentUser TokenParseResponseVO parseVO) {
	    boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
	    
	    return courseService.selectManageCourseList(
	            parseVO.getNoType(), // employeeNo
	            tutor
	    );
	}
}
