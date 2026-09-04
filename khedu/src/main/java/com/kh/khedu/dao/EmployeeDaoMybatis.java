package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.vo.admin.employee.AdminEmployeeDetailVO;
import com.kh.khedu.vo.admin.employee.AdminEmployeeListVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeVO;

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
	@Override
	public EmployeeDetailVO findMyInfo(String accountId) {
		return sqlSession.selectOne("mapper.employee.findMyInfo",accountId);
	}
	@Override
	public EmployeeDto selectOneByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.employee.findEmployeeByAccountNo", accountNo);
	}
	@Override
	public List<AdminEmployeeListVO> selectAdminEmployeeList() {
		return sqlSession.selectList("mapper.employee.selectAdminEmployeeList");
	}
	@Override
	public AdminEmployeeDetailVO selectAdminEmployeeDetailByEmployeeNo(int employeeNo) {
		return sqlSession.selectOne("mapper.employee.selectAdminEmployeeDetailByEmployeeNo", employeeNo);
	}
	
}
