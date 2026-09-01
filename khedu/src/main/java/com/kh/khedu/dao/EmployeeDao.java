package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.employee.EmployeeVO;

public interface EmployeeDao {
	int sequence(); //등록
	void insert(EmployeeVO employeeVO);
	EmployeeDetailVO findMyInfo(String accountId);

	
	//직원의 현재 상태 조회
	String findEmployeeStatus(int employeeNo);
	
	//근로계약 후 대기 -> 재직
	boolean changeUnassignedToWorking(int employeeNo);

	EmployeeDto selectOneByAccountNo(int accountNo);
	
	//이름으로 직원 검색
	List<EmployeeSearchByNameVO> searchByName(String accountName);

}
