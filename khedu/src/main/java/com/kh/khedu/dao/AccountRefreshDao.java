package com.kh.khedu.dao;

import com.kh.khedu.dto.AccountRefreshDto;

public interface AccountRefreshDao {
	void insertOrUpdate(AccountRefreshDto accountRefreshDto);
	void delete(String accountId);
	AccountRefreshDto find(String accountId);
}
