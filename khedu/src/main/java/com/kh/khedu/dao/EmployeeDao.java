package com.kh.khedu.dao;

import java.sql.Timestamp;
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
	
	// 재직 전환 시 계정 활성화
	boolean changeAccountStatusToY(int employeeNo);

	EmployeeDto selectOneByAccountNo(int accountNo);
	
	//이름으로 직원 검색
	List<EmployeeSearchByNameVO> searchByName(String accountName);

	
	// active 계약이 생긴 대기 직원 → 재직
	int activateWaitingEmployees();
	

	// active 계약이 있는 직원 계정 → Y
	int activateEmployeeAccounts();
	
	void updateEmploymentDateIfNull(
	        int employeeNo,
	        Timestamp clockIn
	);
	
}
