package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.student.StudentVO;


@Repository
public class StudentDaoMybatis implements StudentDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.student.sequence");
	}

	@Override
	public void insert(StudentVO studentVO) {
		sqlSession.insert("mapper.student.join", studentVO);
	}

}
