package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.vo.parent.ParentStudentVO;
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

	@Override
	public void expireStudentLink(int studentNo) {
		sqlSession.update("mapper.student.expireStudentLink", studentNo);
	}

	@Override
	public void insertStudentLink(StudentLinkVO studentLinkVO) {
		sqlSession.insert("mapper.student.insertStudentLink", studentLinkVO);
	}

	@Override
	public int sequenceLink() {
		return sqlSession.selectOne("mapper.student.sequenceLink");
	}

}
