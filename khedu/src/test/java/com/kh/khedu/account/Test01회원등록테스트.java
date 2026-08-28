package com.kh.khedu.account;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.employee.EmployeeRegisterRequestVO;
import com.kh.khedu.vo.employee.EmployeeVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test01회원등록테스트 {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private EmployeeDao employeeDao;
	
	@Test
	public void test() {
		
		//고객이 입력해준정보
		//accountNo 생성
		int accountNo = accountDao.sequence();
		EmployeeRegisterRequestVO registerVO = EmployeeRegisterRequestVO.builder()
					.accountId("testuser5@naver.com")
					.accountPassword("Testuser5!")
					.accountName("오모모")
					.accountPhone("01054453333")
					.employeeType("데스크")
					.employeeHtime(LocalDate.of(2026, 8, 25))
					.roleNos(null)
				.build();
		
		//[1] account 테이블 등록
		AccountRegisterVO accountVO = AccountRegisterVO.builder()
					.accountNo(accountNo)
					.accountId(registerVO.getAccountId())
					.accountPassword(registerVO.getAccountPassword())
					.accountName(registerVO.getAccountName())
					.accountPhone(registerVO.getAccountPhone())
				.build();
		sqlSession.insert("mapper.account.register", accountVO);
		
		//[2] 직원 테이블 등록
		//employeeNo 생성
		int employeeNo = employeeDao.sequence();
		
		EmployeeVO employeeVO = EmployeeVO.builder()
					.employeeNo(employeeNo)
					.accountNo(accountVO.getAccountNo())
					.employeeType(registerVO.getEmployeeType())
					.employeeHtime(
						 Timestamp.valueOf(
								 registerVO.getEmployeeHtime().atStartOfDay()
			            )
					)
				.build();
		sqlSession.insert("mapper.employee.register", employeeVO);
	}
}
