package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentLinkDto;
import com.kh.khedu.vo.studentLink.StudentLinkVO;

@Repository
public class StudentLinkDaoMybatis implements StudentLinkDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public boolean expireStudentLink(int studentNo) {
		return sqlSession.update("mapper.studentLink.expireStudentLink", studentNo) > 0;
	}

	@Override
	public void insertStudentLink(StudentLinkVO studentLinkVO) {
		sqlSession.insert("mapper.studentLink.insertStudentLink", studentLinkVO);
	}

	@Override
	public int sequenceLink() {
		return sqlSession.selectOne("mapper.studentLink.sequenceLink");
	}

	@Override
	public StudentLinkDto findByLinkCode(String linkCode) {
		return sqlSession.selectOne("mapper.studentLink.findByLink", linkCode);
	}

	@Override
	public boolean usedLinkCode(int studentLinkNo) {
		return sqlSession.update("mapper.studentLink.updateYN", studentLinkNo) > 0;
	}

}
