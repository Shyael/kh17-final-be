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
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.service.ParentService;
import com.kh.khedu.service.attendance.AttendanceService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.attendance.StudentAttendanceResponseVO;
import com.kh.khedu.vo.course.CourseSelectBarVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.parent.ChangeParentRequestVO;
import com.kh.khedu.vo.parent.ChangeParentResponseVO;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parent.ParentJoinRequestVO;
import com.kh.khedu.vo.parentStudent.ParentStudentRelatioshipUpdateRequestVO;
import com.kh.khedu.vo.studentLink.ParentLinkRequestVO;
import com.kh.khedu.vo.studentLink.ParentLinkResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "학부모 정보 관리 서비스")
@RestController
@RequestMapping("/api/academy/parent")
public class ParentAcademyRestController {
	
	@Autowired
	private ParentService parentService;
	@Autowired
	private AttendanceDao attendanceDao;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private ParentStudentDao parentStudentDao;
	
	//학부모 회원가입
	@ApiResponse(responseCode = "200", description="회원가입 성공")
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountJoinResponseVO join(
			@RequestBody ParentJoinRequestVO request) {
			//회원가입 처리
			AccountJoinResponseVO accountJoinResponseVO 
			 = parentService.joinParent(request);
		return accountJoinResponseVO;
	}
	
	//내 정보라는 건  cookie에 포함된 loginId를 읽으면 된다
	//stateless(무상태) 서버의 세션 대체 방안
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/me", produces= "application/json")
	public ParentDetailVO me(
		@CurrentUser TokenParseResponseVO parseVO
	) {
		ParentDetailVO parentDetailVO = parentService.findMyInfo(parseVO.getAccountId());
		return parentDetailVO; 
	}
	
	//개인정보 수정(본인)
	@PutMapping("/")
	public ChangeParentResponseVO updateAll(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody ChangeParentRequestVO request
	) {
		return parentService.updateMyInfo(request, parseVO);
	}
	
	//비밀번호 확인
	@PostMapping("/password-check")
	public boolean checkPassword(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody CheckPasswordRequestVO request
	) {
		return parentService.checkPassword(request, parseVO);
	}
	
	//연동코드
	@PostMapping("/link-student")
	public ResponseEntity<ParentLinkResponseVO> link(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody ParentLinkRequestVO request
	) {
		ParentLinkResponseVO response = parentService.linkStudent(request, parseVO);
		return ResponseEntity.ok(response);
	}
	
	//관계 수정
	@PutMapping("/relationship")
	public void relationship(
			@RequestBody ParentStudentRelatioshipUpdateRequestVO request,
			@CurrentUser TokenParseResponseVO parseVO) {
		parentService.updateRelationship(parseVO, request);
	}
	
	// 1. 선택한 자녀의 수강 중인 강좌 목록 (상단 셀렉트박스용)
    @GetMapping("/attendance/child/{studentNo}/courses")
    public ResponseEntity<List<CourseSelectBarVO>> getMyCourses(
    		@PathVariable int studentNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	// 내 자녀 목록(children)에 해당 studentNo가 있는 지 검증
    	int parentNo = parseVO.getNoType();
    	
    	if(!parentStudentDao.isChildOfParent(parentNo, studentNo)) {
    		throw new TargetNotfoundException("해당 자녀에 대한 접근 권한이 없습니다");
    	}
    	
        List<CourseSelectBarVO> list = attendanceDao.selectStudentCourseList(studentNo);
        return ResponseEntity.ok(list);
    }

    // 2. 선택한 자녀의 선택한 강좌 출결 상세 및 통계 조회
    @GetMapping("/attendance/child/{studentNo}/course/{courseNo}")
    public ResponseEntity<StudentAttendanceResponseVO> getMyAttendance(
    		@PathVariable int studentNo,
    		@PathVariable int courseNo,
            @CurrentUser TokenParseResponseVO parseVO) {
        
    	int parentNo = parseVO.getNoType();
    	
    	if(!parentStudentDao.isChildOfParent(parentNo, studentNo)) {
    		throw new TargetNotfoundException("해당 자녀에 대한 접근 권한이 없습니다");
    	}
    	StudentAttendanceResponseVO response = attendanceService.getStudentAttendanceDetail(studentNo, courseNo);
        return ResponseEntity.ok(response);
    }
    
}
