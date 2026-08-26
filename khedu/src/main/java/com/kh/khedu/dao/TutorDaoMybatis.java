package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

@Repository
public class TutorDaoMybatis implements TutorDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.tutor.sequence");
	}

	@Override
	public void insert(TutorDto tutorDto) {
		sqlSession.insert("mapper.tutor.insert", tutorDto);
	}

	@Override
	public TutorDto selectOne(int tutorNo) {
		return sqlSession.selectOne("mapper.tutor.selectOne", tutorNo);
	}

	@Override
	public boolean update(TutorDto tutorDto) {
		return sqlSession.update("mapper.tutor.update", tutorDto) > 0;
	}

	@Override
	public boolean delete(int tutorNo) {
		return sqlSession.delete("mapper.tutor.delete", tutorNo) > 0;
	}

	@Override
	public List<TutorListVO> selectList() {
		return sqlSession.selectList("mapper.tutor.selectList");
	}

	@Override
	public List<TutorListVO> selectListBySubject(int academySubjectNo) {
		return sqlSession.selectList(
				"mapper.tutor.selectListBySubject",
				academySubjectNo
		);
	}

	@Override
	public TutorDetailVO selectDetail(int tutorNo) {
		return sqlSession.selectOne(
				"mapper.tutor.selectDetail",
				tutorNo
		);
	}

	@Override
	public List<TutorEmployeeVO> selectAvailableEmployeeList() {
		return sqlSession.selectList(
				"mapper.tutor.selectAvailableEmployeeList"
		);
	}

}