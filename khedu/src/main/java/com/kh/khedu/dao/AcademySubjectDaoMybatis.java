package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AcademySubjectDto;

@Repository
public class AcademySubjectDaoMybatis implements AcademySubjectDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.academySubject.sequence");
	}

	@Override
	public void insert(AcademySubjectDto academySubjectDto) {
		sqlSession.insert("mapper.academySubject.insert", academySubjectDto);
	}

	@Override
	public List<AcademySubjectDto> selectList(int academyNo) {
		return sqlSession.selectList("mapper.academySubject.selectList", academyNo);
	}

	@Override
	public AcademySubjectDto selectOne(int academySubjectNo) {
		return sqlSession.selectOne("mapper.academySubject.selectOne", academySubjectNo);
	}

	@Override
	public boolean update(AcademySubjectDto academySubjectDto) {
		return sqlSession.update("mapper.academySubject.update", academySubjectDto) > 0;
	}

	@Override
	public boolean delete(int academySubjectNo) {
		return sqlSession.delete("mapper.academySubject.delete", academySubjectNo) > 0;
	}

}