package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.service.course.CourseService;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseCreateResponseVO;
import com.kh.khedu.vo.course.CourseDetailResponseVO;
import com.kh.khedu.vo.course.CourseFormDataVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;
import com.kh.khedu.vo.course.CourseSimpleListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "강좌 정보 관리")
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
	public ResponseEntity<CourseCreateResponseVO> courseInsert(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody CourseCreateRequestVO request) {
		CourseCreateResponseVO response = courseService.createCourse(parseVO, request);
		return ResponseEntity.ok().body(response);
	}
	
	//사용가능 강의실 판단
	@ApiResponse(responseCode = "200", description = "사용 가능")
	@PostMapping("/available-classrooms")
	public List<ClassroomWhenRegisterVO> getAvailableClassrooms(
			@RequestBody AvailableClassroomRequestVO request){
		return courseService.getAvailAbleClassrooms(request);
	}
	
	//강사의 사용가능 시간 조회
	@ApiResponse(responseCode = "200", description = "사용 가능")
	@GetMapping("/available-schedules/{employeeNo}")
	public List<ScheduleDto> getAvailableTutor(
			@PathVariable Integer employeeNo
			){
		return courseService.getTutorschedules(employeeNo);
	}
	
	// 강좌 검색조회
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/list")
	public PageResponseVO<CourseListVO> courseList(
			@Valid @ModelAttribute CourseSearchVO search,
			@CurrentUser TokenParseResponseVO parseVO
	){
		if(parseVO == null || parseVO.getRoleNames() == null) {
			throw new TargetNotfoundException();
		}
		
		List<String> roles = parseVO.getRoleNames();
		boolean isStaff = roles.contains(RoleType.ADMIN.getCode())
				|| roles.contains(RoleType.DESK.getCode());
		//권한이 desk, 관리자가 아니고 직원이면
		if(!isStaff && roles.contains(RoleType.TUTOR.getCode())) {
			search.setEmployeeNo(parseVO.getNoType());
		}
		return courseService.selectList(search);
	}
	
	// 로그인한 강사의 진행중인 강의 목록 조회
	@ApiResponse(responseCode = "200", description = "내가 수업중인 강의 목록 조회 성공")
	@GetMapping("/tutor")
	public List<CourseDto> selectListByEmployee(
	        @CurrentUser TokenParseResponseVO parseVO) {
	    return courseDao.selectTeachingListByEmployee(
	            parseVO.getNoType());
	}
	
	//강좌 상세정보
	@ApiResponse(responseCode = "200", description = "강좌 상세페이지 조회 성공")
	@GetMapping("/detail/{courseNo}")
    public ResponseEntity<CourseDetailResponseVO> getCourseDetail(
            @PathVariable int courseNo,
            @CurrentUser TokenParseResponseVO parseVO) {
        
        CourseDetailResponseVO response = courseService.getCourseDetail(courseNo, parseVO);
        return ResponseEntity.ok(response);
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
