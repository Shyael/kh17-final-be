package com.kh.khedu.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.ParentDao;
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dao.StudentLinkDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.dto.ParentStudentDto;
import com.kh.khedu.dto.StudentLinkDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.error.GetOutException;
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
import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;
import com.kh.khedu.vo.parentStudent.ParentStudentRelatioshipUpdateRequestVO;
import com.kh.khedu.vo.studentLink.ParentLinkRequestVO;
import com.kh.khedu.vo.studentLink.ParentLinkResponseVO;

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
	@Autowired
	private StudentLinkDao studentLinkDao;
	@Autowired
	private ParentStudentDao parentStudentDao;
	@Autowired
	private AccountDao accounttDao;
	
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
		//[1] 계정 존재 여부 검사
		AccountDto accountDto = accountDao.selectOne(accountId);
		if(accountDto == null) 
			throw new TargetNotfoundException();
		
		//[2]학부모가 아니면
		if(!accountDto.getAccountType().equals(AccountType.PARENT.getDescription())) 
			throw new WhoAreYouException();
		
		//[3] 학부모 번호로 자녀정보 조회 
		//학부모 정보 조회
		ParentDto parentDto = parentDao.selectOneByAccountNo(accountDto.getAccountNo());		
		ParentDetailVO parentDetailVO = new ParentDetailVO();
		
		//계정 정보 복사
		BeanUtils.copyProperties(accountDto, parentDetailVO);
		//학부모 정보 복사
		BeanUtils.copyProperties(parentDto, parentDetailVO);
		
		//학부모의 모든 자녀 조회
		List<ParentStudentDetailVO> students = 
				parentStudentDao.selectStudentListByParentNo(parentDto.getParentNo());
		
		//자녀 목록 설정
		parentDetailVO.setStudents(students);
		
		return parentDetailVO;
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
	
	// 연동코드 등록
	public ParentLinkResponseVO linkStudent(ParentLinkRequestVO request, TokenParseResponseVO parseVO) {
		//[1] 로그인 한 계정정보
		int accountNo = parseVO.getAccountNo();
		//[2] accountNo로 학부모 조회
		ParentDto parentDto = parentDao.selectOneByAccountNo(accountNo);
		
		if(parentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[3] 연동코드 조회
		StudentLinkDto studentLinkDto = studentLinkDao.findByLinkCode(request.getLinkCode());
			//존재하지 않을 경우
			if(studentLinkDto == null) { 
				throw new TargetNotfoundException();
			}
			//사용된 연동코드일 경우
			if("Y".equals(studentLinkDto.getLinkUsedYn())) { 
				throw new GetOutException();
			}
			//현재시각보다 만료코드가 이전일 경우(포함)
			Timestamp now = new Timestamp(System.currentTimeMillis());
			if(studentLinkDto.getLinkExpire().before(now)) { 
				throw new GetOutException();
			}
			
		//[4] 학생 번호 가져오기
		int studentNo = studentLinkDto.getStudentNo();
		//[4-1] 학생 번호로 학생 이름 가져오기
		String studentName = accountDao.selectOneByStudentNo(studentNo);
		
		//[5] 해당 학생 중복확인
		ParentStudentDto already = 
				parentStudentDao.findByParentStudentNo(
					ParentStudentDto.builder()
							.parentNo(parentDto.getParentNo())
							.studentNo(studentNo)
					.build()
				);
		if(already != null) { //이미 연동되었으므로 아웃
			throw new GetOutException();
		}
		
		//[6] parent_student에 연결
		ParentStudentDto parentStudentDto = ParentStudentDto.builder()
					.parentNo(parentDto.getParentNo())
					.studentNo(studentNo)
					.relationship(request.getRelationship())
				.build(); 
		
		parentStudentDao.insert(parentStudentDto);
		
		//[7] 사용된 연동코드 정리
		studentLinkDao.usedLinkCode(studentLinkDto.getStudentLinkNo());
		
		//[8] 응답
		return ParentLinkResponseVO.builder()
					.studentNo(studentNo)
					.studentName(studentName)
					.relationship(request.getRelationship())
				.build();
	}

	public void updateRelationship(
			TokenParseResponseVO parseVO, 
			ParentStudentRelatioshipUpdateRequestVO request
	) {
		//[1] 계정 조회
		int accountNo = parseVO.getAccountNo();
		AccountDto accountDto = accountDao.selectOneByAccountNo(accountNo);
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[2] 학부모 확인
		if(!accountDto.getAccountType().equals(AccountType.PARENT.getDescription())){
			throw new WhoAreYouException();
		}
		
		//[3] 학부모 조회
		ParentDto parentDto = parentDao.selectOneByAccountNo(accountNo);
		
		if(parentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[4] 학부모-학생 조회
		ParentStudentDto parentStudentDto = parentStudentDao.findParentStudentByStudentNo(request.getStudentNo());
		
		//[5] 관계값 검증
		String relationship = request.getRelationship();
		if(!relationship.equals("부") 
			&& !relationship.equals("모")
			&& !relationship.equals("보호자")
			&& !relationship.equals("기타")) {
			
			throw new GetOutException();
		}
		
		//[6] 관계 수정
		parentStudentDto.setRelationship(relationship);
		boolean result = parentStudentDao.updateReltaionship(parentStudentDto);
		
		//[7] 해당 학생과 연동되어 있지 않은 경우
	    if (!result) {
	        throw new TargetNotfoundException();
	    }
	}
	
	//학생 번호로 학부모 상세 정보 조회 로직 추가
    public ParentDetailVO findParentDetailByStudentNo(int studentNo) {
        return parentDao.findParentDetailByStudentNo(studentNo);
    }
    
    //검색으로 학부모 정보 조회 로직 추가
    public List<ParentDetailVO> searchParents(String keyword) {
        return parentDao.searchParents(keyword);
    }
	
}
