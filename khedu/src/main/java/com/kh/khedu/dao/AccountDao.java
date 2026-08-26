package com.kh.khedu.dao;

import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.vo.register.AccountVO;

public interface AccountDao {
	int sequence(); //등록
	void insert(AccountVO accountVO);
	AccountDto selectone(String accountId);
}
