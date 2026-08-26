package com.kh.khedu.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.service.EmployeeService;
import com.kh.khedu.vo.account.AccountFindResponseVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeRegisterRequestVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "직원 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
	
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private EmployeeService employeeService;
	
	//회원 등록
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping(value ="/", produces = "application/json")
	public void register(@RequestBody EmployeeRegisterRequestVO request){
		//등록 처리는 서비스에서
		employeeService.registerEmployee(request);
	}
	
	//회원 정보를 반환하는 매핑(주의 : 내 정보 아님)
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/{accountId}", produces = "application/json")
	public AccountFindResponseVO find(@PathVariable String accountId) {
		AccountDto accountDto = accountDao.selectone(accountId);
		//아이디가 없으면
		if(accountDto == null) throw new TargetNotfoundException();
		//직원이 아니면
		if(!accountDto.getAccountType().equals("직원")) throw new WhoAreYouException();
		
		AccountFindResponseVO response = new AccountFindResponseVO();
		BeanUtils.copyProperties(accountDto, response);//가능한 항목 복사
		return response;
	}
	
	//내 정보라는 건  cookie에 포함된 loginId를 읽으면 된다
	//stateless(무상태) 서버의 세션 대체 방안
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/me", produces= "application/json")
	public EmployeeDetailVO me(
		@CookieValue(name = "loginId", required = true) String accountId
	) {
		return employeeService.findMyInfo(accountId);
	}
}
