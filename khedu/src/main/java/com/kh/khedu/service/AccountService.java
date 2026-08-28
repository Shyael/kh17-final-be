package com.kh.khedu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.vo.account.AccountRegisterVO;

@Service
public class AccountService {
	
	@Autowired
	private AccountDao accountDao;
	
	public int createAccount(AccountRegisterVO accountVO) {
		
		int accountNo = accountDao.sequence();
		accountVO.setAccountNo(accountNo);
		
		//비밀번호 암호화하여 등록
		accountDao.insert(accountVO);
		
		return accountNo;
	}
}
