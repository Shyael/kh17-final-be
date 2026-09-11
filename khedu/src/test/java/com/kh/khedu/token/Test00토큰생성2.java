package com.kh.khedu.token;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.kh.khedu.configuration.JwtProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test00토큰생성2 {
	
	@Autowired
	private JwtEncoder jwtEncoder;
	@Autowired
	private JwsHeader jwsHeader;
	@Autowired
	private JwtProperties jwtProperties;
	
	@Test
	public void test() {
		
		//[2]
		Instant current = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
					.issuer("https://www.khacademy.co.kr/")
					.issuedAt(current)
					.expiresAt(current.plusSeconds(60))
					.subject("testuser6@naver.com")
					.claim("accountNo", 32)
					.claim("accountId", "testuser6@naver.com")
					.claim("accountNos", List.of(3, 5))
				.build();
		
		
		//최종 생성
		String jwtToken = jwtEncoder
							.encode(JwtEncoderParameters.from(jwsHeader, claims))
							.getTokenValue();
		log.debug("jwt token = {}", jwtToken);
	}
	
}
