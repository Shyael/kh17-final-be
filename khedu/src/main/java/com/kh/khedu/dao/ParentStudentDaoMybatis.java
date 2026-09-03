package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ParentStudentDto;
import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;
import com.kh.khedu.vo.parentStudent.StudentParentDetailVO;

@Repository
public class ParentStudentDaoMybatis implements ParentStudentDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public ParentStudentDto findByParentStudentNo(ParentStudentDto parentStudentDto) {
		return sqlSession.selectOne("mapper.parentStudent.findByParentStudentNo", parentStudentDto);
	}

	@Override
	public void insert(ParentStudentDto parentStudentDto) {
		sqlSession.insert("mapper.parentStudent.insert", parentStudentDto);
	}

	@Override
	public List<ParentStudentVO> findByParentNo(int parentNo) {
		return sqlSession.selectList("mapper.parentStudent.findByParentNo", parentNo);
	}

	@Override
	public List<ParentStudentDetailVO> selectStudentListByParentNo(int parentNo) {
		return sqlSession.selectList("mapper.parentStudent.selectStudentListByParentNo", parentNo);
	}
	
	@Override
	public List<StudentParentDetailVO> selectParentListByStudentNo(int studentNo) {
		return sqlSession.selectList("mapper.parentStudent.selectParentListByStudentNo", studentNo);
	}

	@Override
	public ParentStudentDto findParentStudentByStudentNo(int studentNo) {
		return sqlSession.selectOne("mapper.parentStudent.findParentStudentByStudentNo", studentNo);
	}

	@Override
	public boolean updateReltaionship(ParentStudentDto parentStudentDto) {
		return sqlSession.update("mapper.parentStudent.updateRelationship", parentStudentDto) > 0;
	}

	@Override
	public ParentStudentVO selectOneRelationByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.parentStudent.findParentStudentByAccountNo", accountNo);
	}
	
	@Override
	public boolean deleteRelationship(ParentStudentDto dto) {
	    // 삭제된 행(row)이 1개 이상이면 true 반환
	    return sqlSession.delete("mapper.parentStudent.deleteRelationship", dto) > 0;
	}

}
