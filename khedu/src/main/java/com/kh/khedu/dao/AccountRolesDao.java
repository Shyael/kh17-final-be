package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.vo.roles.RoleVO;

public interface AccountRolesDao {
	void insert(AccountRolesDto accountRolesVO);
	List<Integer> selectRoleNos(int accountNo);
	List<String> selectRoleNames(int accountNo);
	
	// 계정 번호로 해당 계정의 권한객체 목록 불러오기
	List<RoleVO> selectByAccountNo(int accountNo);
}
