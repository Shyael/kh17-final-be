package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.vo.roles.RoleVO;

@Repository
public class AccountRolesDaoMybatis implements AccountRolesDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void insert(AccountRolesDto accountRolesDto) {
		sqlSession.insert("mapper.accountRoles.add", accountRolesDto);
	}

	@Override
	public List<Integer> selectRoleNos(int accountNo) {
		return sqlSession.selectList("mapper.accountRoles.selectRoleNos", accountNo);
	}

	@Override
	public List<String> selectRoleNames(int accountNo) {
		return sqlSession.selectList("mapper.accountRoles.selectRoleNames", accountNo);
	}

	@Override
	public List<RoleVO> selectByAccountNo(int accountNo) {
		return sqlSession.selectList("mapper.accountRoles.selectRoleListByAccountNo", accountNo);
	}

}
