package com.kh.khedu.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.student.StudentJoinRequestVO;
import com.kh.khedu.vo.account.AccountJoinResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//@Tag(name = "학생 정보 관리 서비스")
//@RestController
//@RequestMapping("/api/student")
public class StudentRestController {
	
	//학생 회원가입
//	@ApiResponse(responseCode = "200", description = "등록 성공")
//	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
//	public AccountJoinResponseVO join(
//			@RequestBody StudentJoinRequestVO request) {
//		//회원가입 처리
//		
//		
//	}
}
