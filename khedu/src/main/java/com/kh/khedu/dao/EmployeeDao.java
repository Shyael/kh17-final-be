package com.kh.khedu.dao;

import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeVO;

public interface EmployeeDao {
	int sequence(); //등록
	void insert(EmployeeVO employeeVO);
	EmployeeDetailVO findMyInfo(String accountId);
	EmployeeDto selectOneByAccountNo(int accountNo);
}
