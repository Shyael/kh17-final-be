package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.vo.register.AccountVO;

@Repository
public class AccountDaoMybatis implements AccountDao {
	
	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private PasswordEncoder passwordEncdoer;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.account.sequence");
	}
	
	@Override
	public void insert(AccountVO accountVO) {
		String orgin = accountVO.getAccountPassword(); 
		String encrypt = passwordEncdoer.encode(orgin);
		accountVO.setAccountPassword(encrypt);
		sqlSession.insert("mapper.account.register", accountVO);
	}

	@Override
	public AccountDto selectone(String accountId) {
		// TODO Auto-generated method stub
		return null;
	}

}
