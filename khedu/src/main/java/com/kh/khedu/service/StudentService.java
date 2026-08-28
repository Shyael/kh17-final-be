package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.vo.student.StudentListResponseVO;

@Service
public class StudentService {
	
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private AccountService accountService;
    
    public List<StudentListResponseVO> getStudentList() {
        return studentDao.selectList();
    }
    
//  //직원정보 등록
//  	@Transactional
//  	public void registerStudent(StudentRegisterRequestVO request) {
//  		
//  		// [1] accountService를 호출
//  		AccountRegisterVO accountVO = new AccountRegisterVO();
//  		
//  		BeanUtils.copyProperties(request, accountVO);
//  		
//  		accountVO.setAccountType("직원");
//  		//accoutService에서 등록정보 저장 및 비밀번호 암호화 한 후 accountNo 반환
//  		int accountNo = accountService.createAccount(accountVO);
//  		
//  		// [2] student 생성
//  		int studentNo = studentDao.sequence();
//  		
//  		StudentVO studentVO = StudentList.builder()
//  				.studentNo(studentNo)
//  				.accountNo(accountNo)
//  				.studentType(request.getStudentType())
//  				.studentHtime(
//  					 Timestamp.valueOf(
//  							 request.getStudentHtime().atStartOfDay()
//  		            )
//  				)
//  				.build();
//  		studentDao.insert(studentVO);
//  		
//  		//[3] student의 권한 등록 
//  		List<Integer> roleNos = request.getRoleNos();
//  		for(Integer roleNo : roleNos) {
//  			AccountRolesDto accountRolesDto = AccountRolesDto.builder()
//  					.accountNo(accountNo)
//  					.roleNo(roleNo)
//  					.build();
//  			accountRolesDao.insert(accountRolesDto);
//  		}
//  	}
//  	
//  	//직원정보 조회
//  	public StudentDetailVO findMyInfo(String accountId) {
//  		
//  		//계정 존재 여부 검사
//  		AccountDto accountDto = accountDao.selectOne(accountId);
//  			//아이디가 없으면
//  		if(accountDto == null) throw new TargetNotfoundException();
//  			//직원이 아니면
//  		if(!accountDto.getAccountType().equals("직원")) throw new WhoAreYouException();
//  		
//  		return studentDao.findMyInfo(accountId);
//  	}
}