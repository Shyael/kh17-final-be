package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.TutorSubjectDto;

@Repository
public class TutorSubjectDaoMybatis implements TutorSubjectDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.tutorSubject.sequence");
	}

	@Override
	public void insert(TutorSubjectDto tutorSubjectDto) {
		sqlSession.insert(
				"mapper.tutorSubject.insert",
				tutorSubjectDto
		);
	}

	@Override
	public List<TutorSubjectDto> selectList(int tutorNo) {
		return sqlSession.selectList(
				"mapper.tutorSubject.selectList",
				tutorNo
		);
	}

	@Override
	public TutorSubjectDto selectOne(int tutorSubjectNo) {
		return sqlSession.selectOne(
				"mapper.tutorSubject.selectOne",
				tutorSubjectNo
		);
	}

	@Override
	public boolean update(TutorSubjectDto tutorSubjectDto) {
		return sqlSession.update(
				"mapper.tutorSubject.update",
				tutorSubjectDto
		) > 0;
	}

	@Override
	public boolean delete(int tutorSubjectNo) {
		return sqlSession.delete(
				"mapper.tutorSubject.delete",
				tutorSubjectNo
		) > 0;
	}

}