package com.kh.khedu.service.payroll;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
@Service
public class ContractAuthorizationServiceImpl implements ContractAuthorizationService{
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private ContractDao contractDao;
	
	public boolean checkAdmin(TokenParseResponseVO parseVO) {
		List<String> permission = parseVO.getRoleNames();
		boolean isAdmin =permission.contains("admin");
		return isAdmin;
	}
	
	public boolean checkPartyB(TokenParseResponseVO parseVO,long contractNo) {
		
		ContractDto find = contractDao.find(contractNo);
		String id = parseVO.getAccountId();
		int compare = find.getEmployeeNo();
		
		EmployeeDetailVO employee = employeeDao.findMyInfo(id);
		
		int no = employee.getAccountNo();
		
		boolean isPartyB = no==compare;
		return isPartyB;
	}
	
	public boolean checkAdminOrPartyB(TokenParseResponseVO parseVO,long contractNo) {
		
		List<String> permission = parseVO.getRoleNames();
		boolean isAdmin =permission.contains("admin");
		
		
		ContractDto find = contractDao.find(contractNo);
		String id = parseVO.getAccountId();
		int compare = find.getEmployeeNo();
		
		EmployeeDetailVO employee = employeeDao.findMyInfo(id);
		
		int no = employee.getAccountNo();
		
		boolean isPartyB = no==compare;
		
		boolean isAdminOrPartyB = isAdmin || isPartyB;
		return isAdminOrPartyB;
	}
	
	public boolean checkAdminOrPartyBOrDeskByContract(TokenParseResponseVO parseVO, long contractNo) {
	
		List<String> permission = parseVO.getRoleNames();
		boolean isAdmin =permission.contains("admin");
		
		boolean isDesk =permission.contains("desk");
		
		ContractDto find = contractDao.find(contractNo);
		String id = parseVO.getAccountId();
		int compare = find.getEmployeeNo();
		
		EmployeeDetailVO employee = employeeDao.findMyInfo(id);
		
		int no = employee.getAccountNo();
		
		boolean isPartyB = no==compare;
		
		boolean isAdminOrPartyBOrDesk = (isAdmin||isDesk) || (isAdmin||isPartyB) || (isPartyB||isDesk);
		
		return isAdminOrPartyBOrDesk;
		
	}

	@Override
	public boolean checkAdminOrPartyBOrDeskByEmployee(
	        TokenParseResponseVO parseVO,
	        int employeeNo) {

	    // 원장은 허용
	    if (checkAdmin(parseVO))
	        return true;

	    // 데스크는 허용
	    List<String> roleNames = parseVO.getRoleNames();

	    if (roleNames != null && roleNames.contains("desk"))
	        return true;

	    // 로그인한 계정에 연결된 직원번호 조회
	    Integer loginEmployeeNo =
	            contractDao.findEmployeeNoByAccountNo(
	                    parseVO.getAccountNo()
	            );

	    // 직원이 아니거나 연결정보가 없으면 거부
	    if (loginEmployeeNo == null)
	        return false;

	    // 조회 대상 직원 본인인지 확인
	    return loginEmployeeNo == employeeNo;
	}
}
