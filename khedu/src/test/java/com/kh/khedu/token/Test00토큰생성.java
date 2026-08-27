package com.kh.khedu.token;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test00토큰생성 {
	
	@Test
	public void test() {
		//[1]
		String secret = "12345678901234567890123456789012";
		SecretKey key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		
		//[2]
		JwtClaimsSet claims = JwtClaimsSet.builder()
					.claim("accountNo", 32)
					.claim("accountId", "testuser6@naver.com")
					.claim("accountNos", List.of(3, 5))
				.build();
		
		//[3]
		//JWT 토큰 생성기를 만든다
		JwtEncoder jwtEncoder =
				new NimbusJwtEncoder(new ImmutableSecret<>(key));
		
		//JWT 헤더 생성
		JwsHeader jwsHeader = JwsHeader
								.with(MacAlgorithm.HS256)
							.build();
		
		//최종 생성
		String jwtToken = jwtEncoder
							.encode(JwtEncoderParameters.from(jwsHeader, claims))
							.getTokenValue();
		log.debug("jwt token = {}", jwtToken);
	}
	
}
