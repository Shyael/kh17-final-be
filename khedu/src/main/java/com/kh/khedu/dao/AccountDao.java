package com.kh.khedu.dao;

import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.vo.account.AccountVO;

public interface AccountDao {
	int sequence(); //등록
	void insert(AccountVO accountVO);
	AccountDto selectOne(String accountId);
	
	//아이디 중복검사 (가능하면 true)
	boolean checkAvailableId(String accountId);
}
