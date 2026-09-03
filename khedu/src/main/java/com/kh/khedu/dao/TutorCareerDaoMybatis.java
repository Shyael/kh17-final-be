package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.TutorCareerDto;

@Repository
public class TutorCareerDaoMybatis implements TutorCareerDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.tutorCareer.sequence");
	}

	@Override
	public void insert(TutorCareerDto tutorCareerDto) {
		sqlSession.insert(
				"mapper.tutorCareer.insert",
				tutorCareerDto
		);
	}

	@Override
	public List<TutorCareerDto> selectList(int tutorNo) {
		return sqlSession.selectList(
				"mapper.tutorCareer.selectList",
				tutorNo
		);
	}

	@Override
	public TutorCareerDto selectOne(int tutorCareerNo) {
		return sqlSession.selectOne(
				"mapper.tutorCareer.selectOne",
				tutorCareerNo
		);
	}

	@Override
	public boolean update(TutorCareerDto tutorCareerDto) {
		return sqlSession.update(
				"mapper.tutorCareer.update",
				tutorCareerDto
		) > 0;
	}

	@Override
	public boolean delete(int tutorCareerNo) {
		return sqlSession.delete(
				"mapper.tutorCareer.delete",
				tutorCareerNo
		) > 0;
	}

}