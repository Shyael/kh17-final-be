package com.kh.khedu.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dto.AccountRolesDto;
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
	private AccountRolesDao accountRolesDao;
	@Autowired
	private AccountService accountService;
	
	@Transactional
	public void registerEmployee(EmployeeRegisterRequestVO request) {
		
		// [1] accountService를 호출
		AccountVO accountVO = new AccountVO();
		
		BeanUtils.copyProperties(request, accountVO);
		
		accountVO.setAccountType("직원");
		//accoutService에서 등록정보 저장 및 비밀번호 암호화 한 후 accountNo 반환
		int accountNo = accountService.createAccount(accountVO);
		
		// [2] employee 생성
		int employeeNo = employeeDao.sequence();
		
		EmployeeVO employeeVO = EmployeeVO.builder()
				.employeeNo(employeeNo)
				.accountNo(accountVO.getAccountNo())
				.employeeType(request.getEmployeeType())
				.employeeHtime(
					 Timestamp.valueOf(
							 request.getEmployeeHtime().atStartOfDay()
		            )
				)
				.build();
		employeeDao.insert(employeeVO);
		
		//[3] employee의 권한 연결 
		List<Integer> roleNos = request.getRoleNos();
		for(Integer roleNo : roleNos) {
			AccountRolesDto accountRolesDto = AccountRolesDto.builder()
					.accountNo(accountNo)
					.roleNo(roleNo)
					.build();
			accountRolesDao.insert(accountRolesDto);
		}
	}
}
