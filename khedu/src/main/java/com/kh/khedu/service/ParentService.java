package com.kh.khedu.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.ParentDao;
import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.parent.ParentJoinRequestVO;

@Service
public class ParentService {
	@Autowired
	private AccountService accountService;
	@Autowired
	private ParentDao parentDao;
	@Autowired
	private AccountRolesDao accountRolesDao;
	
	//학부모 정보 등록
	@Transactional
	public AccountJoinResponseVO joinParent(ParentJoinRequestVO request) {
		// [1] account정보 담기
		AccountRegisterVO accountVO = new AccountRegisterVO();
		
		BeanUtils.copyProperties(request, accountVO);
		
		System.out.println("request = " + request);
		System.out.println("request password = " + request.getAccountPassword());

		System.out.println("accountVO = " + accountVO);
		System.out.println("accountVO password = " + accountVO.getAccountPassword());
		
		accountVO.setAccountType(AccountType.PARENT.getDescription());
		
		// accountService에서 account관련 정보 등록 (비밀번호는 암호화해서)
		int accountNo = accountService.createAccount(accountVO);
		
		// [2] 학부모 등록
		int parentNo = parentDao.sequence();
		
		ParentDto parentDto = ParentDto.builder()
					.parentNo(parentNo)
					.accountNo(accountNo)
				.build();
		parentDao.insert(parentDto);
		
		// [3] 권한 등록
		int roleNo = RoleType.PARENT.getRoleNo();
		AccountRolesDto accountRolesDto = AccountRolesDto.builder()
					.accountNo(accountNo)
					.roleNo(roleNo)
				.build();
		accountRolesDao.insert(accountRolesDto);
		
		//[4] 결과 반환
		return AccountJoinResponseVO.builder()
				.accountNo(accountNo)
				.targetNo(parentNo)
				.accountId(accountVO.getAccountId())
				.accountName(accountVO.getAccountName())
				.accountType(accountVO.getAccountType())
				.message("학부모 등록 신청이 완료되었습니다. 관리자 승인 후 로그인이 가능합니다.")
			.build();
	}

}
