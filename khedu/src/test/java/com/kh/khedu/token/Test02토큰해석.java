package com.kh.khedu.token;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.khedu.service.JwtService;
import com.kh.khedu.vo.jwt.TokenCreateRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test02토큰해석 {
	
	@Autowired
	private JwtService jwtService;
	
	@Test
	public void test() {
		String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjZAbmF2ZXIuY29tIiwiYWNjb3VudElkIjoidGVzdHVzZXI2QG5hdmVyLmNvbSIsImFjY291bnRObyI6MzIsImFjY291bnRUeXBlIjoi7KeB7JuQIiwiaXNzIjoiaHR0cHM6Ly93d3cua2hhY2FkZW15LmNvLmtyLyIsImV4cCI6MTc4Nzc0NzQxMSwiaWF0IjoxNzg3NzQ1NjExLCJyb2xlTm9zIjpbMyw1XX0.p7qnqZFNpO-mZ5lLxmwcgz4uMy8oU2wfku9XQqBdl30";
		
		TokenParseResponseVO response = jwtService.parseAccessToken(jwtToken);
		
		log.debug("accountNo = {}", response.getAccountNo());
		log.debug("accountId = {}", response.getAccountId());
		log.debug("accountType = {}", response.getAccountType());
		log.debug("roleNos = {}", response.getRoleNames());
	}
	
}
