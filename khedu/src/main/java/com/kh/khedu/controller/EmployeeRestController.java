package com.kh.khedu.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.service.EmployeeService;
import com.kh.khedu.vo.account.AccountFindResponseVO;
import com.kh.khedu.vo.account.ChangePasswordRequestVO;
import com.kh.khedu.vo.account.ChangePasswordResponseVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
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
	private EmployeeService employeeService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	//직원 등록
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping(value ="/", produces = "application/json")
	public ResponseEntity<Void> register(@RequestBody EmployeeRegisterRequestVO request){
		//등록 처리는 서비스에서
		employeeService.registerEmployee(request);
		return ResponseEntity.ok().build();
	}
	
	//아이디(=이메일) 중복검사 - 사용가능하면 true, 불가능하면 false를 반환
	@ApiResponse(responseCode = "200", description = "존재하는 아이디")
	@GetMapping(value ="/check-id/{accountId}", produces = "application/json")
	public boolean checkAccountId(@PathVariable String accountId) {
		System.out.println("===== 아이디 중복검사 실행 =====");
	    System.out.println("accountId = " + accountId);
		return accountDao.checkAvailableId(accountId);
	}
	
	//직원 정보를 반환하는 매핑(주의 : 내 정보 아님)
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/{accountId}", produces = "application/json")
	public AccountFindResponseVO find(@PathVariable String accountId) {
		AccountDto accountDto = accountDao.selectOne(accountId);
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
		//@CookieValue(name = "accesstoken", required = false) String accessToken
		@CurrentUser TokenParseResponseVO parseVO
	) {
		return employeeService.findMyInfo(parseVO.getAccountId());
	}
	
	// 직원 비밀번호 변경 요청에 대한 처리
	@PatchMapping("/password")
	public ChangePasswordResponseVO  password
	(
		@CurrentUser TokenParseResponseVO parseVO,
		@Valid @RequestBody ChangePasswordRequestVO request
	) {
		//[1] DB에서 기존 유저의 정보를 불러온다
		AccountDto accountDto = accountDao.selectOne(parseVO.getAccountId());
		if(accountDto == null) throw new TargetNotfoundException();
		
		//[2] 비밀번호를 비교한다
		String db = accountDto.getAccountPassword(); //DB
		String input = request.getPrevAccountPassword(); //사용자 입력
		boolean valid = passwordEncoder.matches(input, db); //BCrypt비교
		if(!valid) {//비밀번호 가 틀리면
			return ChangePasswordResponseVO.builder()
					.result(false)
					.message("비밀번호가 일치하지 않습니다")
					.build();
		}
		
		//[3] 동일한 비밀번호로 변경을 차단
		boolean same = request.getPrevAccountPassword().equals(request.getNewAccountPassword());
		if(same) {
			return ChangePasswordResponseVO.builder()
					.result(false)
					.message("동일한 비밀번호로는 변경이 불가능합니다")
					.build();
		}
		
		//[4] 형식 검사
		String regex = "^(?=.*?[A-Z]+)(?=.*?[a-z]+)(?=.*?[0-9]+)(?=.*?[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\[\\]\\{\\}\\'\\\"\\`\\~\\<\\>\\.\\,\\/\\?\\\\\\|]+)[A-Za-z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\=\\+\\[\\]\\{\\}\\'\\\"\\`\\~\\<\\>\\.\\,\\/\\?\\\\\\|]{8,16}$";
		if(request.getNewAccountPassword().matches(regex) == false) {
			return ChangePasswordResponseVO.builder()
					.result(false)
					.message("비밀번호는 대문자, 소문자, 숫자, 특수문자를 반드시 포함하여 변경해야합니다")
				.build();
		}
		
		//[5] 변경 시도
		accountDao.updateAccountPassword(AccountDto.builder()
					.accountNo(parseVO.getAccountNo())
					.accountPassword(request.getNewAccountPassword())
				.build());
		
		//[6] 성공 알림
		return ChangePasswordResponseVO.builder()
				.result(true)
				.message("비밀번호 변경이 완료되었습니다")
				.build();
	}
}
