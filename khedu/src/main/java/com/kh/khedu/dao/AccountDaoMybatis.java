package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.account.AccountTypeNoVO;
import com.kh.khedu.vo.account.FindAccountIdRequestVO;
import com.kh.khedu.vo.account.FindAccountPasswordRequestVO;

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
	public void insert(AccountRegisterVO accountVO) {
		String orgin = accountVO.getAccountPassword(); 
		String encrypt = passwordEncdoer.encode(orgin);
		accountVO.setAccountPassword(encrypt);
		sqlSession.insert("mapper.account.register", accountVO);
	}

	@Override
	public AccountDto selectOne(String accountId) {
		return sqlSession.selectOne("mapper.account.find", accountId);
	}

	@Override
	public boolean checkAvailableId(String accountId) {
		int count = sqlSession.selectOne("mapper.account.checkAccountId", accountId);
		return count == 0;
	}

	@Override
	public AccountTypeNoVO selectTypeNo(int accountNo) {
		return sqlSession.selectOne("mapper.account.findCorrectly", accountNo);
	}

	@Override
	public boolean updateAccountPassword(AccountDto accountDto) {
		String orgin = accountDto.getAccountPassword(); 
		String encrypt = passwordEncdoer.encode(orgin);
		accountDto.setAccountPassword(encrypt);
		return sqlSession.update("mapper.account.updateAccountPassword", accountDto) > 0;
	}

	@Override
	public AccountDto findAccountId(FindAccountIdRequestVO request) {
		return sqlSession.selectOne("mapper.account.findAccountId", request);
	}

	@Override
	public AccountDto findAccountPassword(FindAccountPasswordRequestVO request) {
		return sqlSession.selectOne("mapper.account.findPasswordAccount", request);
	}
}
