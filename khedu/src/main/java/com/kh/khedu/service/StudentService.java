package com.kh.khedu.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO;
import com.kh.khedu.vo.student.StudentVO;

@Service
public class StudentService {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private AccountRolesDao accountRolesDao;
	
	//학생 정보 등록
	@Transactional
	public AccountJoinResponseVO joinStudent(StudentJoinRequestVO request) {
		
		// [1] account정보 담기
		AccountRegisterVO accountVO = new AccountRegisterVO();
		
		BeanUtils.copyProperties(request, accountVO);
		
		accountVO.setAccountType(AccountType.STUDENT.getDescription());
		
		// accountService에서 account관련 정보 등록 (비밀번호는 암호화해서)
		int accountNo = accountService.createAccount(accountVO);
		
		// [2] 상담 정보 등록 
		// 상담테이블에서 이름과 핸드폰 번호로 가져오기(map형태나 vo만들어서 나중에 하기)
		//int consultCustommerNo = consultCustomerDao.selectOne(accountVO.getAccountPhone(), accountVO.getAccountName());
		
		// [3] student 등록
		int studentNo = studentDao.sequence();
		
		StudentVO studentVO = StudentVO.builder()
					.studentNo(studentNo)
					.accountNo(accountNo)
					.consultCustomerNo(null)
					.studentSchool(request.getStudentSchool())
					.studentGrade(request.getStudentGrade())
					.studentGender(request.getStudentGender())
					.studentEtc(request.getStudentEtc())
				.build();
		studentDao.insert(studentVO);
		
		// [4] 권한 등록
		int roleNo = RoleType.STUDENT.getRoleNo();
		AccountRolesDto accountRolesDto = AccountRolesDto.builder()
					.accountNo(accountNo)
					.roleNo(roleNo)
				.build();
		accountRolesDao.insert(accountRolesDto);
		
		//[5] 결과 반환
		return AccountJoinResponseVO.builder()
				.accountNo(accountNo)
				.targetNo(studentNo)
				.accountId(accountVO.getAccountId())
				.accountName(accountVO.getAccountName())
				.accountType(accountVO.getAccountType())
				.message("학생 등록 신청이 완료되었습니다. 데스크 승인 후 서비스 이용이 가능합니다.")
			.build();
	}
}
