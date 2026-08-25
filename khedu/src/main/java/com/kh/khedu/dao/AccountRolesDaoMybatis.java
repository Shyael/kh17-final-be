package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AccountRolesDto;

@Repository
public class AccountRolesDaoMybatis implements AccountRolesDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void insert(AccountRolesDto accountRolesDto) {
		sqlSession.insert("mapper.accountRoles.add", accountRolesDto);
	}

}
