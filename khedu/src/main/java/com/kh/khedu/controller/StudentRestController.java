package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.service.StudentService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
	
}
