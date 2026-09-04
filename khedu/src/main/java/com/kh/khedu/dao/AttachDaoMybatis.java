package com.kh.khedu.dao;

import java.util.List;

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

	@Override
	public AttachDto selectOne(Integer attachNo) {
		if(attachNo == null) return null;
		return sqlSession.selectOne("mapper.attach.selectOne", attachNo);
	}

	@Override
	public List<AttachDto> selectList(List<Integer> attachNumbers) {
		if(attachNumbers == null || attachNumbers.isEmpty()) return List.of();
		return sqlSession.selectList("mapper.attach.findList", attachNumbers);
	}

}
