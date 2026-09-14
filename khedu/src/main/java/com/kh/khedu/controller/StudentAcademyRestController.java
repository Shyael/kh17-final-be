package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.AttendanceDao;
import com.kh.khedu.service.StudentService;
import com.kh.khedu.service.attendance.AttendanceService;
import com.kh.khedu.service.course.CourseService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.attendance.StudentAttendanceResponseVO;
import com.kh.khedu.vo.course.CourseSelectBarVO;
import com.kh.khedu.vo.course.StudentCourseListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.student.ChangeStudentRequestVO;
import com.kh.khedu.vo.student.ChangeStudentResponseVO;
import com.kh.khedu.vo.student.StudentDetailVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO;
import com.kh.khedu.vo.studentLink.StudentLinkResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "학생 정보 관리 서비스")
@RestController
@RequestMapping("/api/academy/student")
public class StudentAcademyRestController {
	@Autowired 
	private StudentService studentService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private AttendanceDao attendanceDao;
	@Autowired
	private AttendanceService attendanceService;
	
	//학생 회원가입
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountJoinResponseVO join(
			@RequestBody StudentJoinRequestVO request) {
		//회원가입 처리
		AccountJoinResponseVO accountJoinResponseVO = studentService.joinStudent(request);
		
		return accountJoinResponseVO;
		
	}
	
	//내 정보라는 건  cookie에 포함된 loginId를 읽으면 된다
	//stateless(무상태) 서버의 세션 대체 방안
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/me", produces= "application/json")
	public StudentDetailVO me(
		@CurrentUser TokenParseResponseVO parseVO
	) {
		StudentDetailVO studentDetailVO = studentService.findMyInfo(parseVO.getAccountId());
		return studentDetailVO; 
	}
	
	//개인정보 수정(본인)
	@PutMapping("/")
	public ChangeStudentResponseVO updateAll(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody ChangeStudentRequestVO request
	) {
		return studentService.updateMyInfo(request, parseVO);
	}
	
	//비밀번호 확인
	@PostMapping("/password-check")
	public boolean checkPassword(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody CheckPasswordRequestVO request
	) {
		return studentService.checkPassword(request, parseVO);
	}
	
	//연동코드
	@PostMapping("/link")
	public StudentLinkResponseVO link(
			@CurrentUser TokenParseResponseVO parseVO
	) {
		return studentService.createStudentLink(parseVO);
	}
	
	//학생 본인이 수강중인 강의 목록
	@Operation(summary = "학생 수강중인 강의 목록 조회")
	@GetMapping("/course")
	public List<StudentCourseListVO> selectListByStudent(
	        @CurrentUser TokenParseResponseVO parseVO) {
	    return courseService.selectListByStudent(
	            parseVO.getNoType()
	    );
	}


	//학부모용 : 자녀가 수강중인 강의 목록
	@Operation(summary = "학부모용 자녀 수강중인 강의 목록 조회")
	@GetMapping("/parent/{studentNo}/course")
	public List<StudentCourseListVO> selectListByParentStudent(
	        @PathVariable int studentNo,
	        @CurrentUser TokenParseResponseVO parseVO) {
	    return courseService.selectListByParentStudent(
	            parseVO.getNoType(), //parentNo
	            studentNo
	    );
	}
		
	// 1. 학생 본인이 수강 중인 강좌 목록 (상단 셀렉트박스용)
    // 호출 URL: GET /api/academy/student/attendance/my-courses
    @GetMapping("/attendance/my-courses")
    public ResponseEntity<List<CourseSelectBarVO>> getMyCourses(@CurrentUser TokenParseResponseVO parseVO) {
        int studentNo = parseVO.getNoType();
        List<CourseSelectBarVO> list = attendanceDao.selectStudentCourseList(studentNo);
        return ResponseEntity.ok(list);
    }

    // 2. 선택한 강좌의 출결 상세 및 통계 조회
    // 호출 URL: GET /api/academy/student/attendance/course/{courseNo}
    @GetMapping("/attendance/course/{courseNo}")
    public ResponseEntity<StudentAttendanceResponseVO> getMyAttendance(
            @PathVariable int courseNo,
            @CurrentUser TokenParseResponseVO parseVO) {
        int studentNo = parseVO.getNoType();
        StudentAttendanceResponseVO response = attendanceService.getStudentAttendanceDetail(studentNo, courseNo);
        return ResponseEntity.ok(response);
    }
	
}
