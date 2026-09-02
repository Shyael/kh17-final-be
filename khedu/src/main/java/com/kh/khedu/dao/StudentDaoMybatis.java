package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;
import com.kh.khedu.vo.student.StudentVO;
import com.kh.khedu.vo.studentLink.StudentLinkVO;


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

	@Override
	public StudentDto selectOne(int accountNo) {
		return sqlSession.selectOne("mapper.student.findByAccountNo", accountNo);
	}

	@Override
	public boolean updateAll(StudentDto studentDto) {
		return sqlSession.update("mapper.student.updateAll", studentDto) > 0;
	}

	@Override
	public ParentStudentVO selectOneRelationByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.student.findParentStudentByAccountNo", accountNo);
	}
}
