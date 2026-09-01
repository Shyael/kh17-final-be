package com.kh.khedu.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.ParentDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.dto.EmployeeDto;
import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.parent.ChangeParentRequestVO;
import com.kh.khedu.vo.parent.ChangeParentResponseVO;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parent.ParentJoinRequestVO;
import com.kh.khedu.vo.parent.ParentStudentVO;

@Service
public class ParentService {
	@Autowired
	private AccountService accountService;
	@Autowired
	private ParentDao parentDao;
	@Autowired
	private AccountRolesDao accountRolesDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//학부모 정보 등록
	@Transactional
	public AccountJoinResponseVO joinParent(ParentJoinRequestVO request) {
		// [1] account정보 담기
		AccountRegisterVO accountVO = new AccountRegisterVO();
		
		BeanUtils.copyProperties(request, accountVO);

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

	//학부모 정보 조회
	public ParentDetailVO findMyInfo(String accountId) {
		//계정 존재 여부 검사
		AccountDto accountDto = accountDao.selectOne(accountId);
		//아이디가 없으면
		if(accountDto == null) throw new TargetNotfoundException();
		//학부모가 아니면
		if(!accountDto.getAccountType().equals(AccountType.PARENT.getDescription())) throw new WhoAreYouException();
		
		//[2] 학부모학생테이블에 학생정보가 없는경우
		ParentStudentVO parentStudentVO = parentDao.selectOneRelationByAccountNo(accountDto.getAccountNo());
		ParentDetailVO parentDetailVO = new ParentDetailVO();
		
		ParentDto parentDto = parentDao.selectOneByAccountNo(accountDto.getAccountNo());		
		
		if(parentStudentVO == null) { //학생정보가 없으면
			BeanUtils.copyProperties(parentDto, parentDetailVO); //학부모 정보
			BeanUtils.copyProperties(accountDto, parentDetailVO); //계정 정보
			
			return parentDetailVO;
		}
		
		//[3] 학부모학생테이블에 학생정보가 있는 경우
		BeanUtils.copyProperties(accountDto, parentDetailVO); //계정 정보
		return ParentDetailVO.builder()
					.parentNo(parentDto.getParentNo())
					.studentNo(parentStudentVO.getStudentNo())
					.relationship(parentStudentVO.getRelationship())
				.build();
	}

	//학부모 정보 수정(본인)
	public ChangeParentResponseVO updateMyInfo(
			ChangeParentRequestVO request,
			TokenParseResponseVO parseVO) {
		//[1] 정보조회 후 없으면 404처리
		int accountNo = parseVO.getAccountNo();
		AccountDto accountDto = accountDao.selectOneByAccountNo(accountNo);
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[2] 학부모인지 확인
		ParentDto parentDto = parentDao.selectOneByAccountNo(accountNo);
		
		if(parentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[3] 입력된 값이 있다면, account 수정 // 이름 연락처 생일 수정일
		BeanUtils.copyProperties(request, accountDto);
		accountDao.updateAll(accountDto);
		
		//[4] 수정된 정보 다시 조회
		AccountDto resultAccount = accountDao.selectOneByAccountNo(accountNo);
		
		//[5] 필요한 값 세팅
		ChangeParentResponseVO response = new ChangeParentResponseVO();
		BeanUtils.copyProperties(resultAccount, response);
		return response;
	}
	
	//비밀번호 확인
	public boolean checkPassword(
			CheckPasswordRequestVO request,
			TokenParseResponseVO parseVO
	) {
		int accountNo = parseVO.getAccountNo();
		//[1] 정보조회 후 없으면 404처리
		AccountDto accountDto = accountDao.selectOneByAccountNo(accountNo);
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[2] 학부모인지 확인
		ParentDto parentDto = parentDao.selectOneByAccountNo(accountNo);
				
		if(parentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[3] 비밀번호 확인
		return passwordEncoder
			.matches(
				request.getAccountPassword(), //입력된 비번
				accountDto.getAccountPassword() //DB
		);
	}
	
}
