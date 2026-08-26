package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AcademyDto;

@Repository
public class AcademyDaoMybatis implements AcademyDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.academy.sequence");
	}

	@Override
	public void insert(AcademyDto academyDto) {
		sqlSession.insert("mapper.academy.insert", academyDto);
	}

	@Override
	public AcademyDto selectOne() {
		return sqlSession.selectOne("mapper.academy.selectOne");
	}

	@Override
	public boolean update(AcademyDto academyDto) {
		return sqlSession.update("mapper.academy.update", academyDto) > 0;
	}
	

	@Override
	public boolean delete(int academyNo) {
		return sqlSession.delete("mapper.academy.delete", academyNo) > 0;
	}
	
}
