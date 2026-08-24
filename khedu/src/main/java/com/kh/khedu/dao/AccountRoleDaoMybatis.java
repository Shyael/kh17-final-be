package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.register.AccountRoleVO;

@Repository
public class AccountRoleDaoMybatis implements AccountRoleDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void insert(AccountRoleVO accountRoleVO) {
		sqlSession.insert("mapper.accountRoles.add", accountRoleVO);
	}

}
