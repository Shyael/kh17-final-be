package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parent.ParentSearchVO;
import com.kh.khedu.vo.parent.ParentUpdateRequestVO;
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
	public ParentStudentVO selectOneRelationByAccountNo(Integer accountNo) {
		return sqlSession.selectOne("mapper.parent.findParentStudentByAccountNo", accountNo);
	}

	@Override
	public ParentDto selectOneByAccountNo(Integer accountNo) {
		return sqlSession.selectOne("mapper.parent.findByAccountNo", accountNo);
	}
	
	@Override
	public List<ParentDetailVO> findParentDetailByStudentNo(Integer studentNo) {
		return sqlSession.selectList("mapper.parent.findParentDetailByStudentNo", studentNo);
	}
	
	@Override
	public List<ParentDetailVO> searchParents(String keyword) {
	    return sqlSession.selectList("mapper.parent.searchParents", keyword);
	}

	
	// 관리자 
	
	// 학부모 수
	@Override
	public int count(ParentSearchVO searchVO) {
		return sqlSession.selectOne("mapper.parent.count", searchVO);
	}

	// 학부모 검색
	@Override
	public List<ParentDetailVO> list(ParentSearchVO searchVO) {
		return sqlSession.selectList("mapper.parent.list", searchVO);
	}
	
	// 학부모 승인
	@Override
	public boolean approveParent(int parentNo) {
		return sqlSession.update("mapper.parent.approveParent", parentNo) > 0;
	}
	
	@Override
	public ParentDetailVO detail(int parentNo) {
	    return sqlSession.selectOne("mapper.parent.detail", parentNo);
	}

	@Override
	public boolean updateAccountInfo(ParentUpdateRequestVO requestVO) {
	    return sqlSession.update("mapper.parent.updateAccountInfo", requestVO) > 0;
	}

}