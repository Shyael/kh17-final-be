package com.kh.khedu.dao;

import com.kh.khedu.vo.register.EmployeeVO;

public interface EmployeeDao {
	int sequence(); //등록
	void insert(EmployeeVO employeeVO);
}
