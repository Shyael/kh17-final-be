package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;

@Repository
public class ParentDaoMybatis implements ParentDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.parent.sequence");
	}

	@Override
	public void insert(ParentDto parentDto) {
		sqlSession.insert("mapper.parent.join", parentDto);
	}

	@Override
	public ParentStudentVO selectOneRelationByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.parent.findParentStudentByAccountNo", accountNo);
	}

	@Override
	public ParentDto selectOneByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.parent.findByAccountNo", accountNo);
	}
	
	@Override
	public ParentDetailVO findParentDetailByStudentNo(int studentNo) {
		return sqlSession.selectOne("mapper.parent.findParentDetailByStudentNo", studentNo);
	}

}