package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.service.AuthService;
import com.kh.khedu.vo.auth.AuthLoginRequestVO;
import com.kh.khedu.vo.auth.AuthLoginResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="인증 처리 서비스", description = "stateless 서버의 인증 처리 로직 구현")
@CommonsApiResponse

@RestController
@RequestMapping("/service/auth")
public class AuthRestController {
	@Autowired
	private AuthService authService;
	
	
	@ApiResponse(responseCode = "200", description = "로그인 성공")
	@ApiResponse(responseCode = "400", description = "정보 불일치")
	@PostMapping(value ="/login", produces = "application/json")
	public AuthLoginResponseVO login(@RequestBody AuthLoginRequestVO request) {
		System.out.println("프론트에서 넘어온 데이터: " + request);
		return authService.login(request);
	}
}
