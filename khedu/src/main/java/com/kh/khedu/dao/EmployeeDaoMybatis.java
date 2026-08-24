package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.register.EmployeeVO;

@Repository
public class EmployeeDaoMybatis implements EmployeeDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.employee.sequence");
	}
	@Override
	public void insert(EmployeeVO employeeVO) {
		sqlSession.insert("mapper.employee.register", employeeVO);
	}
	
}
