package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.vo.admin.employee.AdminEmployeeDetailVO;
import com.kh.khedu.vo.admin.employee.AdminEmployeeListVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeVO;

public interface EmployeeDao {
	int sequence(); //등록
	void insert(EmployeeVO employeeVO);
	EmployeeDetailVO findMyInfo(String accountId);
	EmployeeDto selectOneByAccountNo(int accountNo);
	
	//관리자 
	// [1] 직원 목록
	List<AdminEmployeeListVO> selectAdminEmployeeList();
	
	// [2] 직원 상세
	AdminEmployeeDetailVO selectAdminEmployeeDetailByEmployeeNo(int employeeNo);
}
