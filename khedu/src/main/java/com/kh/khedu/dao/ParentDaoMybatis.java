package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ParentDto;

@Repository
public class ParentDaoMybatis implements ParentDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.parent.sequence");
	}

	@Override
	public void insert(ParentDto parentDto) {
		sqlSession.insert("mapper.parent.join", parentDto);
	}

}
