package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AccountRolesDto;

public interface AccountRolesDao {
	void insert(AccountRolesDto accountRolesVO);
	List<Integer> selectRoleNos(int accountNo);
	List<String> selectRoleNames(int accountNo);
}
