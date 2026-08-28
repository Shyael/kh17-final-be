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
	
	public boolean checkAdminOrPartyBOrDesk(TokenParseResponseVO parseVO, long contractNo) {
	
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
}
