package com.kh.khedu.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.student.StudentListResponseVO;

@Repository 
public class StudentDaoMybatis implements StudentDao {

    @Autowired
    private SqlSession sqlSession; 
    
    @Override
    public List<StudentListResponseVO> selectList() {
        return sqlSession.selectList("student.list");
    }
    
    @Override
	public int sequence() {
		return sqlSession.selectOne("mapper.employee.sequence");
	}
}