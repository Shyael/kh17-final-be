package com.kh.khedu.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.service.EmployeeService;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.employee.ChangeEmployeeRequestVO;
import com.kh.khedu.vo.employee.ChangeEmployeeResponseVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeMeResponseVO;
import com.kh.khedu.vo.employee.EmployeeRegisterRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "직원 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee")
public class EmployeeRestController {
	
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeService employeeService;
	
	//직원 등록
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping(value ="/", produces = "application/json")
	public ResponseEntity<Void> register(@RequestBody EmployeeRegisterRequestVO request){
		//등록 처리
		employeeService.registerEmployee(request);
		return ResponseEntity.ok().build();
	}
	
	//직원 정보를 반환하는 매핑(주의 : 내 정보 아님)
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/{accountId}", produces = "application/json")
	public EmployeeMeResponseVO find(@PathVariable String accountId) {
		AccountDto accountDto = accountDao.selectOne(accountId);
		//아이디가 없으면
		if(accountDto == null) throw new TargetNotfoundException();
		//직원이 아니면
		if(!accountDto.getAccountType().equals("직원")) throw new WhoAreYouException();
		
		EmployeeMeResponseVO response = new EmployeeMeResponseVO();
		BeanUtils.copyProperties(accountDto, response);//가능한 항목 복사
		//employeeType 정보 추가로 넣기
		String employeeType = employeeDao.selectOneByAccountNo(accountDto.getAccountNo()).getEmployeeType();
		response.setEmployeeType(employeeType);
		
		return response;
	}
	
	//내 정보라는 건  cookie에 포함된 loginId를 읽으면 된다
	//stateless(무상태) 서버의 세션 대체 방안
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/me", produces= "application/json")
	public EmployeeDetailVO me(
		@CurrentUser TokenParseResponseVO parseVO
	) {
		EmployeeDetailVO employeeDetailVO = employeeService.findMyInfo(parseVO.getAccountId());
		return employeeDetailVO; 
	}
	
	//개인정보 수정(본인)
	@PutMapping("/")
	public ChangeEmployeeResponseVO updateAll(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody ChangeEmployeeRequestVO request
	) {
		return employeeService.updateMyInfo(request, parseVO);
	}
	
	//비밀번호 확인
	@PostMapping("/password-check")
	public boolean checkPassword(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody CheckPasswordRequestVO request
	) {
		return employeeService.checkPassword(request, parseVO);
	}
	
}
