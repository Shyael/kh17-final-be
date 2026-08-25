package com.kh.khedu.account;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kh.khedu.dto.AccountDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test01로그인테스트 {
	
	@Autowired
	private PasswordEncoder passwordEncdoer;
	
	@Autowired
	private SqlSession sqlSession;
	
	@Test
	public void test() {
		//로그인에 필요한 데이터를 준비
		String accountId = "testuser5@naver.com";
		String accountPassword = "Testuser5!";
		
		//아이디로 회원 정보를 모두 조회
		AccountDto findAccountDto = sqlSession.selectOne(
			"mapper.account.find", 
			AccountDto.builder()
				.accountId(accountId)
				.accountPassword(accountPassword)
			.build()
		);
		if(findAccountDto == null) {
			log.error("아이디가 존재하지 않습니다");
			return;
		}
		
//		//비밀번호 비교
//		boolean match = passwordEncdoer.matches(
//				accountPassword, //사용자가 입력한 값
//				findAccountDto.getAccountPassword() //변경된값(기존 저장된 비번)
//		);
//		if(match == false) {
//			log.error("비밀번호가 일치하지 않습니다");
//			return;
//		}
		
		//최종 로그인 판정
		log.debug("로그인 성공");
	}
}
