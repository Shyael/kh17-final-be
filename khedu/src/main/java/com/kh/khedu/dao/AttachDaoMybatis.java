package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AttachDto;

@Repository
public class AttachDaoMybatis implements AttachDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.attach.sequence");
	}

	@Override
	public void insert(AttachDto attachDto) {
		sqlSession.insert("mapper.attach.insert", attachDto);
	}

	@Override
	public AttachDto selectOne(int attachNo) {
		return sqlSession.selectOne("mapper.attach.selectOne", attachNo);
	}

	@Override
	public boolean delete(int attachNo) {
		return sqlSession.delete("mapper.attach.delete", attachNo) > 0;
	}

}
