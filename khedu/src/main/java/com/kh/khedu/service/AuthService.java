package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.account.AccountTypeNoVO;
import com.kh.khedu.vo.auth.AuthLoginRequestVO;
import com.kh.khedu.vo.auth.AuthLoginResponseVO;
import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;

//인증과 관련된 복잡한 작업들을 모듈화 하여 처리하기 위한 서비스
@Service
public class AuthService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountRolesDao accountRolesDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ParentStudentDao parentStudentDao;
	
	//로그인 처리
	public AuthLoginResponseVO login(AuthLoginRequestVO request) {
		
		AccountDto accountDto = accountDao.selectOne(request.getAccountId());
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
//		//로그인 유형 검사(회원로그인 창 or 직원 로그인 창)
//		if(AccountType.EMPLOYEE.getDescription().equals(request.getLoginType())) {
//			//로그인페이지가 직원인데
//			
//			if(!AccountType.EMPLOYEE.getDescription().equals(accountDto.getAccountType())) {
//				//계정의 유형이 직원이 아니면
//				throw new TargetNotfoundException();
//			}
//			
//		}
//		if(AccountType.MEMBER.getDescription().equals(request.getLoginType())) {
//			//로그인페이지가 회원인데
//			
//			if(!AccountType.STUDENT.getDescription().equals(accountDto.getAccountType())
//				&& !AccountType.PARENT.getDescription().equals(accountDto.getAccountType())) {
//				//계정 유형이 학생, 학부모가 아니면
//				throw new TargetNotfoundException();
//			}
//			
//		}
		
		//비밀번호 비교
		boolean valid = passwordEncoder.matches(
				request.getAccountPassword(), accountDto.getAccountPassword());
		if(!valid) throw new TargetNotfoundException();
		
		//차단 회원이라면?
		if(accountDto.getAccountStatus().equals("N")) {
			throw new GetOutException("승인되지 않은 회원입니다");
		}
		
		//비밀번호 변경한 지 30일 지난 경우
		
		//accountNo를 통해 roleNo도 가져오기 (계정번호를 통해 계정에 할당된 권한(role_no)가져오기)
		//List<Integer> roleNos = accountRolesDao.selectRoleNos(accountDto.getAccountNo());
		//accountNo를 통해 roleName가져오기
		List<String> roleNames =accountRolesDao.selectRoleNames(accountDto.getAccountNo());
		//accountNo를 토대로 typeNo가져오기
		AccountTypeNoVO accountTypeNoVO = accountDao.selectTypeNo(accountDto.getAccountNo());
		Integer typeNo = null;
		List<ParentStudentDetailVO> children = null;
		
		if ("직원".equals(accountTypeNoVO.getAccountType())) {
		    typeNo = accountTypeNoVO.getEmployeeNo();
		}
		else if ("학생".equals(accountTypeNoVO.getAccountType())) {
		    typeNo = accountTypeNoVO.getStudentNo();
		}
		else if ("학부모".equals(accountTypeNoVO.getAccountType())) {
		    typeNo = accountTypeNoVO.getParentNo();
		    //학부모일 경우 자녀 목록까지 넘기기
		    children = parentStudentDao.selectStudentListByParentNo(typeNo);
		}
		
		
		//로그인 성공
		return AuthLoginResponseVO.builder()
				.accountNo(accountDto.getAccountNo())
				.accountId(accountDto.getAccountId())
				.accountName(accountDto.getAccountName())
				.accountType(accountDto.getAccountType())
				.typeNo(typeNo)
				.roleNames(roleNames)
				.children(children)
			.build();
	}
}
