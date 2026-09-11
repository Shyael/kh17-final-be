package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.CertDto;

@Repository
public class CertDaoMybatis implements CertDao{	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void add(CertDto certDto) {
		sqlSession.insert("mapper.cert.add", certDto);
	}

	@Override
	public boolean change(CertDto certDto) {
		int rows = sqlSession.update("mapper.cert.change", certDto);
		return rows > 0;
	}

	@Override
	public CertDto find(String certEmail) {
		return sqlSession.selectOne("mapper.cert.find", certEmail);
	}

	@Override
	public boolean delete(String certEmail) {
		int rows = sqlSession.delete("mapper.cert.delete", certEmail);
		return rows > 0;
	}

	@Override
	public boolean use(String certEmail) {
		int rows = sqlSession.update("mapper.cert.use", certEmail);
		return rows > 0;
	}
	
}
