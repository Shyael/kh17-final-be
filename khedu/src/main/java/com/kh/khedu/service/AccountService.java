package com.kh.khedu.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.account.FindAccountPasswordRequestVO;

import jakarta.mail.MessagingException;

@Service
public class AccountService {
	
	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private RandomService randomService;
	
	@Autowired
	private EmailService emailService;
	
	public int createAccount(AccountRegisterVO accountVO) {
		
		int accountNo = accountDao.sequence();
		accountVO.setAccountNo(accountNo);
		
		//비밀번호 암호화하여 등록
		accountDao.insert(accountVO);	
		
		return accountNo;
	}
	
	//비밀번호 초기화
	public void resetPassword(FindAccountPasswordRequestVO request) throws MessagingException, IOException {
		
		// 1. 계정 조회
		AccountDto accountDto = accountDao.findAccountPassword(request);
		
		// 입력받은 정보가 없는경우
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
		// 2. 임시비밀번호 생성
		String tempPassword = randomService.generatePassword(8);
		
		// 3. DB 비밀번호 변경
		boolean result = accountDao.updateAccountPassword(
				AccountDto.builder()
					.accountNo(accountDto.getAccountNo())
					.accountPassword(tempPassword)
				.build()
		);
		
		if(!result) {
			throw new RuntimeException();
		}
		
		//[4] 이메일 발송
		emailService.sendTempPassword(accountDto.getAccountId(), tempPassword);
	}
}
