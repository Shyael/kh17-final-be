package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.vo.admin.employee.AdminEmployeeDetailVO;
import com.kh.khedu.vo.admin.employee.AdminEmployeeListVO;
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
	
	//관리자 
	// [1] 직원 목록
	List<AdminEmployeeListVO> selectAdminEmployeeList();
	
	// [2] 직원 상세
	AdminEmployeeDetailVO selectAdminEmployeeDetailByEmployeeNo(int employeeNo);
	//이름으로 직원 검색
	List<EmployeeSearchByNameVO> searchByName(String accountName);

	
	
	
	void updateEmploymentDateIfNull(
	        int employeeNo,
	        Timestamp clockIn
	);
	//고용일자 조회
	Timestamp findEmploymentDate(long employeeNo);
	//직원이 본인이 맞는지 확인
	boolean checkEmployeeOwner(
			int accountNo,
			int employeeNo
	);
}
