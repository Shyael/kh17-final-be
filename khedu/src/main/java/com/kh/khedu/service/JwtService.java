package com.kh.khedu.service;

import org.springframework.stereotype.Service;

import com.kh.khedu.vo.jwt.TokenCreateRequestVO;

@Service
public class JwtService {
	
	//토큰 생성 메소드
	public String createToken(TokenCreateRequestVO request) {
		return "?";
	}
	
//	//토큰 해석 메소드
//	public TokenParseResponseVO parseResponseVO() {
//		
//	}
}
