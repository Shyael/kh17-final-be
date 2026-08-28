package com.kh.khedu.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;

import com.kh.khedu.configuration.JwtProperties;
import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.vo.jwt.TokenCreateRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public class JwtService {
	
	@Autowired
	private JwtEncoder jwtEncoder;
	
	@Autowired
	private JwsHeader jwsHeader;
	
	@Autowired
	private JwtProperties jwtProperties;
	
	@Autowired
	private JwtDecoder jwtDecoder;
	
	//액세스 토큰 생성 메소드
	public String createAccessToken(TokenCreateRequestVO request) {
		//토큰 발생시각을 객체로 생성
		
		Instant current = Instant.now();
		
		//JWT에 추가할 데이터 본문을 생성
		JwtClaimsSet claims = JwtClaimsSet.builder()
				//표준 데이터 - iss, iat, exp, sub
				.issuer(jwtProperties.getIssuer())
				.issuedAt(current)
				.expiresAt(current.plusSeconds(jwtProperties.getAccessTokenValidity()))
				.subject(request.getAccountId())
				//커스텀 데이터 - 마음대로
				.claim("accountNo", request.getAccountNo())
				.claim("accountId", request.getAccountId())
				.claim("accountType", request.getAccountType())
				.claim("roleNames", request.getRoleNames())
				.claim("noType", request.getTypeNo())
				//spring Security 검사를 위한 항목을 추가
				//- 이름은 authorities 고정 → hasAuthority()로 검사
				//- 이름을 roles로 설정하면 → hasRoles()로 검사 (ROLE_접두사 필요)
				//.claim("authorities", request.getRoleNos())
				.claim( "authorities", request.getRoleNames())
			.build();
		//토큰 최종 생성 및 결과 반환
		return jwtEncoder
				.encode(JwtEncoderParameters.from(jwsHeader, claims))
				.getTokenValue();
	}
	
	//액세스 토큰 해석 메소드
	public TokenParseResponseVO parseAccessToken(String token) throws JwtValidationException {
		//모두 검사 후 정보추출 (문제가 생기면 JwtValidationException 발생)
		Jwt jwt = jwtDecoder.decode(token);
		return TokenParseResponseVO.builder()
					.accountNo(((Long)jwt.getClaim("accountNo")).intValue())
					.accountId(jwt.getClaimAsString("accountId"))
					.accountType(jwt.getClaimAsString("accountType"))
					.roleNames(jwt.getClaim("roleNames"))
				.build();
	}
	
	//액세스 토큰 해석 메소드
	public TokenParseResponseVO parseAccessToken(Jwt jwt) {
		return TokenParseResponseVO.builder()
				.accountNo(((Long)jwt.getClaim("accountNo")).intValue())
				.accountId(jwt.getClaimAsString("accountId"))
				.accountType(jwt.getClaimAsString("accountType"))
				.roleNames(jwt.getClaim("roleNames"))
			.build();
	}
	
	//리프레시 토큰 생성 메소드(액세스와 다르게 customData가 없음)
	public String createRefreshToken(String accountId) {
		//토큰 발생시각을 객체로 생성
		Instant current = Instant.now();
		
		//JWT에 추가할 데이터 본문을 생성
		JwtClaimsSet claims = JwtClaimsSet.builder()
				//표준 데이터 - iss, iat, exp, sub
				.issuer(jwtProperties.getIssuer())
				.issuedAt(current)
				.expiresAt(current.plusSeconds(
						jwtProperties.getRefreshTokenValidity()
						)) //4주
				.subject(accountId)
			.build();
		
		//토큰 최종 생성 및 DB저장 + 결과 반환
		return jwtEncoder
				.encode(JwtEncoderParameters.from(jwsHeader, claims))
				.getTokenValue();
	}
	//리프레시 토큰 해석 메소드
	public String parseRefreshToken(String token) {
		//모두 검사 후 정보추출 (문제가 생기면 JwtValidationException 발생)
		Jwt jwt = jwtDecoder.decode(token);
		return jwt.getSubject();
	}
}
