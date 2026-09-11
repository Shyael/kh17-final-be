package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.grade.GradeVO;

@Repository
public class GradeDaoMybatis implements GradeDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public List<GradeVO> selectList() {
		return sqlSession.selectList("mapper.grade.gradeList");
	}

}
