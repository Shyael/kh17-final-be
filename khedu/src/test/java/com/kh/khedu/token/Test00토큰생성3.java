package com.kh.khedu.token;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.khedu.service.JwtService;
import com.kh.khedu.vo.jwt.TokenCreateRequestVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test00토큰생성3 {
	
	@Autowired
	private JwtService jwtService;
	
	@Test
	public void test() {
		String jwtToken = jwtService.createToken(
				TokenCreateRequestVO.builder()
					.accountId("testuser6@naver.com")
					.accountNo(32)
					.accountType("직원")
					.roleNos(List.of(3, 5))
				.build()
		);
		log.debug("jwt token = {}", jwtToken);
	}
	
}
