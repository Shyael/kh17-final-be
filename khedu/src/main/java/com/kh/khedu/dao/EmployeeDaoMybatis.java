package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
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

	public String findEmployeeStatus(int employeeNo) {
		return sqlSession.selectOne("mapper.employee.findEmployeeStatus",employeeNo);
	}
	@Override
	public boolean changeUnassignedToWorking(int employeeNo) {
		return sqlSession.update("mapper.employee.changeUnassignedToWorking",employeeNo)>0;
	}
	@Override
	public EmployeeDto selectOneByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.employee.findEmployeeByAccountNo", accountNo);

	}
	//이름으로 직원 검색
	@Override
	public List<EmployeeSearchByNameVO> searchByName(String accountName){
		return sqlSession.selectList("mapper.employee.searchByName",accountName);
	}
	
	
	@Override
	public boolean changeAccountStatusToY(int employeeNo) {

	    return sqlSession.update(
	            "mapper.employee.changeAccountStatusToY",
	            employeeNo
	    ) > 0;
	}
	
	
	@Override
	public int activateWaitingEmployees() {

	    return sqlSession.update(
	            "mapper.employee.activateWaitingEmployees"
	    );
	}


	@Override
	public int activateEmployeeAccounts() {

	    return sqlSession.update(
	            "mapper.employee.activateEmployeeAccounts"
	    );
	}
	
	//첫 출근시에 고용일자 업데이트
	@Override
	public void updateEmploymentDateIfNull(
	        int employeeNo,
	        Timestamp clockIn) {

	    Map<String, Object> params =
	            new HashMap<>();

	    params.put("employeeNo", employeeNo);
	    params.put("clockIn", clockIn);

	    sqlSession.update(
	            "mapper.employee.updateEmploymentDateIfNull",
	            params
	    );
	}
}
