package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AcademyHistoryDto;

@Repository
public class AcademyHistoryDaoMybatis implements AcademyHistoryDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.academyHistory.sequence");
	}

	@Override
	public void insert(AcademyHistoryDto academyHistoryDto) {
		sqlSession.insert("mapper.academyHistory.insert", academyHistoryDto);
	}

	@Override
	public List<AcademyHistoryDto> selectList(int academyNo) {
		return sqlSession.selectList("mapper.academyHistory.selectList", academyNo);
	}

	@Override
	public AcademyHistoryDto selectOne(int academyHistoryNo) {
		return sqlSession.selectOne("mapper.academyHistory.selectOne", academyHistoryNo);
	}

	@Override
	public boolean update(AcademyHistoryDto academyHistoryDto) {
		return sqlSession.update("mapper.academyHistory.update", academyHistoryDto) > 0;
	}

	@Override
	public boolean delete(int academyHistoryNo) {
		return sqlSession.delete("mapper.academyHistory.delete", academyHistoryNo) > 0;
	}

}
