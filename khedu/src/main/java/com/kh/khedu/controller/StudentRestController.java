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
	@Autowired
	private AccountDao accountDao;
	
	//학생 회원가입
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountJoinResponseVO join(
			@RequestBody StudentJoinRequestVO request) {
		//회원가입 처리
		AccountJoinResponseVO accountJoinResponseVO = studentService.joinStudent(request);
		
		return accountJoinResponseVO;
		
	}
	
	//아이디(=이메일) 중복검사 - 사용가능하면 true, 불가능하면 false를 반환
	@ApiResponse(responseCode = "200", description = "존재하는 아이디")
	@GetMapping(value ="/check-id/{accountId}", produces = "application/json")
	public boolean checkAccountId(@PathVariable String accountId) {
		System.out.println("===== 아이디 중복검사 실행 =====");
	    System.out.println("accountId = " + accountId);
		return accountDao.checkAvailableId(accountId);
	}
}
