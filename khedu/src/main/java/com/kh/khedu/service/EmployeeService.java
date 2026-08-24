package com.kh.khedu.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRoleDao;
import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.vo.register.AccountRoleVO;
import com.kh.khedu.vo.register.AccountVO;
import com.kh.khedu.vo.register.EmployeeRegisterRequestVO;
import com.kh.khedu.vo.register.EmployeeVO;

@Service
public class EmployeeService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private AccountRoleDao accountRoleDao;
	
	@Transactional
	public void registerEmployee(EmployeeRegisterRequestVO request) {
		
		// [1] account 생성
		AccountVO accountVO = new AccountVO();
		int accountNo = accountDao.sequence();
		accountVO.setAccountNo(accountNo);
		
		BeanUtils.copyProperties(request, accountVO);
		
		//비밀번호 암호화하여 등록
		accountDao.insert(accountVO);
		
		// [2] employee 생성
		EmployeeVO employeeVO = new EmployeeVO();
		int employeeNo = employeeDao.sequence();
		employeeVO.setEmployeeNo(employeeNo);
		
		employeeVO.setAccountNo(accountVO.getAccountNo());
		employeeVO.setEmployeeType(request.getEmployeeType());
		employeeDao.insert(employeeVO);
		
		//[3] employee 권한 부여
		AccountRoleVO accountRoleVO = new AccountRoleVO();
		
		accountRoleVO.setAccountNo(accountNo);
		accountRoleVO.setRoleNo(request.getRoleNo());
		accountRoleDao.insert(accountRoleVO);
	}
}
