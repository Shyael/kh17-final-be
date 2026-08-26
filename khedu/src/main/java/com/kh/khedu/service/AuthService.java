package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.auth.AuthLoginRequestVO;
import com.kh.khedu.vo.auth.AuthLoginResponseVO;

//인증과 관련된 복잡한 작업들을 모듈화 하여 처리하기 위한 서비스
@Service
public class AuthService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountRolesDao accountRolesDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//로그인 처리
	public AuthLoginResponseVO login(AuthLoginRequestVO request) {
		
		AccountDto accountDto = accountDao.selectOne(request.getAccountId());
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
		//비밀번호 비교
		boolean valid = passwordEncoder.matches(
				request.getAccountPassword(), accountDto.getAccountPassword());
		if(!valid) throw new TargetNotfoundException();
		
		//차단 회원이라면?
		if(accountDto.getAccountStatus().equals("N")) {
			throw new GetOutException("차단된 회원입니다");
		}
		
		//비밀번호 변경한 지 30일 지난 경우
		
		//accountNo를 통해 roleNo도 가져오기 (계정번호를 통해 계정에 할당된 권한(role_no)가져오기)
		List<Integer> roleNos = accountRolesDao.selectRoleNos(accountDto.getAccountNo());
		
		//로그인 성공
		return AuthLoginResponseVO.builder()
				.accountNo(accountDto.getAccountNo())
				.accountId(accountDto.getAccountId())
				.accountName(accountDto.getAccountName())
				.accountType(accountDto.getAccountType())
				.roleNos(roleNos)
			.build();
	}
}
