package com.kh.khedu.dao;

import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.account.AccountTypeNoVO;
import com.kh.khedu.vo.account.FindAccountIdRequestVO;
import com.kh.khedu.vo.account.FindAccountPasswordRequestVO;

import jakarta.validation.Valid;

public interface AccountDao {
	int sequence(); //등록
	void insert(AccountRegisterVO accountVO);
	AccountDto selectOne(String accountId);
	
	//아이디 중복검사 (가능하면 true)
	boolean checkAvailableId(String accountId);
	
	//계정 유형에 따른 유형 및 유형번호(직원/학생/학부모) 조회
	AccountTypeNoVO  selectTypeNo(int accountNo);
	
	//비밀번호 변경
	boolean updateAccountPassword(AccountDto accountDto);
	
	//이름과 전화번호를 통해 아이디 찾기
	AccountDto findAccountId(FindAccountIdRequestVO request);
	AccountDto findAccountPassword(FindAccountPasswordRequestVO request);
	
	//accountNo로 찾기
	AccountDto selectOneByAccountNo(int accountNo);
	
	
}
