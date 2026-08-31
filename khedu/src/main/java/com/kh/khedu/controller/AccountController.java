package com.kh.khedu.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kh.khedu.service.AccountService;
import com.kh.khedu.vo.account.ChangePasswordRequestVO;
import com.kh.khedu.vo.account.ChangePasswordResponseVO;
import com.kh.khedu.vo.account.FindAccountIdRequestVO;
import com.kh.khedu.vo.account.FindAccountIdResponseVO;
import com.kh.khedu.vo.account.FindAccountPasswordRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@Tag(name = "계정 정보 관리 서비스")
@RestController
@RequestMapping("/api/account")
public class AccountController {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AccountService accountService;
	
	//아이디(=이메일) 중복검사 - 사용가능하면 true, 불가능하면 false를 반환
	@ApiResponse(responseCode = "200", description = "존재하는 아이디")
	@GetMapping(value ="/check-id/{accountId}", produces = "application/json")
	public boolean checkAccountId(@PathVariable String accountId) {
		return accountDao.checkAvailableId(accountId);
	}
	
	// 비밀번호 변경 요청에 대한 처리
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
	
	//아이디 찾기
	@ApiResponse(responseCode = "200", description = "아이디 찾기 성공")
	@PostMapping("/find-id")
	public FindAccountIdResponseVO findId(
			@Valid @RequestBody FindAccountIdRequestVO request) {
		
		//입력받은 이름과 전화번호를 통해 계정정보 조회
		AccountDto accountDto = accountDao.findAccountId(request);
		
		//아이디가 없으면
		if(accountDto.getAccountId() == null) {
			return FindAccountIdResponseVO.builder()
						.result(false)
						.message("입력하신 정보와 일치하는 계정이 없습니다")
					.build();
		}
		
		//찾기 성공
		return FindAccountIdResponseVO.builder()
				.result(true)
				.message("아이디를 찾았습니다")
				.accountId(accountDto.getAccountId())
			.build();
	}
	
	//비밀번호 찾기(리셋)
	@ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공 및 이메일 발송")
	@PostMapping("/find-password")
	public void findPassword(
			@Valid @RequestBody FindAccountPasswordRequestVO request) throws MessagingException, IOException {
		
		accountService.resetPassword(request);
	}
}
