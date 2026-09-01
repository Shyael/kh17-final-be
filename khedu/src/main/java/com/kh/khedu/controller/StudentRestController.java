package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.StudentService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.student.ChangeStudentRequestVO;
import com.kh.khedu.vo.student.ChangeStudentResponseVO;
import com.kh.khedu.vo.student.StudentDetailVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO;
import com.kh.khedu.vo.studentLink.StudentLinkResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "학생 정보 관리 서비스")
@RestController
@RequestMapping("/api/student")
public class StudentRestController {
	@Autowired 
	private StudentService studentService;
	
	
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
}
