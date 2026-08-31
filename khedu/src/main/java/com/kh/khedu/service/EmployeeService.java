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
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.employee.ChangeEmployeeRequestVO;
import com.kh.khedu.vo.employee.ChangeEmployeeResponseVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeRegisterRequestVO;
import com.kh.khedu.vo.employee.EmployeeVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

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
	
	//직원정보 등록
	@Transactional
	public void registerEmployee(EmployeeRegisterRequestVO request) {
		
		// [1] account정보 담기
		AccountRegisterVO accountVO = new AccountRegisterVO();
		
		BeanUtils.copyProperties(request, accountVO);
		
		//accountType : '직원' 등록
		accountVO.setAccountType(AccountType.EMPLOYEE.getDescription());
		//accoutService에서 등록정보 저장 및 비밀번호 암호화 한 후 accountNo 반환
		int accountNo = accountService.createAccount(accountVO);
		
		// [2] employee 등록
		int employeeNo = employeeDao.sequence();
		
		EmployeeVO employeeVO = EmployeeVO.builder()
				.employeeNo(employeeNo)
				.accountNo(accountNo)
				.employeeType(request.getEmployeeType())
				.employeeHtime(
					 Timestamp.valueOf(
							 request.getEmployeeHtime().atStartOfDay()
		            )
				)
				.build();
		employeeDao.insert(employeeVO);
		
		//[3] employee의 권한 등록 
		List<Integer> roleNos = request.getRoleNos();
		for(Integer roleNo : roleNos) {
			AccountRolesDto accountRolesDto = AccountRolesDto.builder()
					.accountNo(accountNo)
					.roleNo(roleNo)
					.build();
			accountRolesDao.insert(accountRolesDto);
		}
	}
	
	//직원정보 조회
	public EmployeeDetailVO findMyInfo(String accountId) {
		
		//계정 존재 여부 검사
		AccountDto accountDto = accountDao.selectOne(accountId);
			//아이디가 없으면
		if(accountDto == null) throw new TargetNotfoundException();
			//직원이 아니면
		if(!accountDto.getAccountType().equals("직원")) throw new WhoAreYouException();
		
//		return employeeDao.findMyInfo(accountId);
		EmployeeDetailVO employeeDetailVO = new EmployeeDetailVO();
		BeanUtils.copyProperties(accountDto, employeeDetailVO);
		
		return employeeDetailVO;
	}
	
	//직원정보 수정(본인)
//	public ChangeEmployeeResponseVO updateMyInfo(
//			ChangeEmployeeRequestVO request,
//			TokenParseResponseVO parseVO) {
//		int accountNo = parseVO.getAccountNo();
//		//[1] 정보조회 후 없으면 404처리
//		AccountDto accountDto = accountDao.selectOneByAccountNo(accountNo);
//		if(accountDto == null) {
//			throw new TargetNotfoundException();
//		}
//		
//		//[2] 직원인지 확인
//		EmployeeDto employeeDto = employeeDao.selectOneByAccountNo(accountNo);
//		
//		if(employeeDto == null) {
//			throw new TargetNotfoundException();
//		}
//		
//	}
}
