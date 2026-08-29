package com.kh.khedu.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.student.StudentJoinRequestVO;
import com.kh.khedu.student.StudentVO;
import com.kh.khedu.vo.account.AccountRegisterVO;

@Service
public class StudentService {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private StudentDao studentDao;
	
	//학생 정보 등록
	public void joinStudent(StudentJoinRequestVO request) {
		
		// [1] account정보 담기
		AccountRegisterVO accountVO = new AccountRegisterVO();
		
		BeanUtils.copyProperties(request, accountVO);
		
		accountVO.setAccountType(AccountType.STUDENT.getDescription());
		
		//accountService에서 account관련 정보 등록 (비밀번호는 암호화해서)
		int accountNo = accountService.createAccount(accountVO);
		
		// [2] student 등록
		int studentNo = studentDao.sequence();
		
		StudentVO studentVO = StudentVO.builder()
					.studentNo(studentNo)
					.accountNo(accountNo)
					.consultCustomerNo(request.getConsultCustomerNo())
					.studentSchool(request.getStudentSchool())
					.studentGrade(request.getStudentGrade())
					.studentGender(request.getStudentGender())
					.studentEtc(request.getStudentEtc())
				.build();
		
		// [3] 학생 권한 등록
//		List<Integer> roleNos = request
	}
}
