package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AccountRefreshDto;

@Repository
public class AccountRefreshDaoMybatis implements AccountRefreshDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void insertOrUpdate(AccountRefreshDto accountRefreshDto) {
		//mybatis는 네이밍홀더를 쓰기 때문에 accountRefreshDto에서 accountId가 선택되어 꺼내진다
		AccountRefreshDto findDto = sqlSession.selectOne("mapper.accountRefresh.find", accountRefreshDto);
		if(findDto == null) { //없으니까 insert
			sqlSession.insert("mapper.accountRefresh.add", accountRefreshDto);
		}
		else {//있으니까 update
			sqlSession.update("mapper.accountRefresh.change", accountRefreshDto);
		}
	}

	@Override
	public void delete(String accountId) {
		sqlSession.delete("mapper.accountRefresh.delete", accountId);
	}

	@Override
	public AccountRefreshDto find(String accountId) {
		return sqlSession.selectOne("mapper.accountRefresh.find", accountId);
	}

}
