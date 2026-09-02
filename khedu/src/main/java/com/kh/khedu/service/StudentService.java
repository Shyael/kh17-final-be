package com.kh.khedu.service;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.AccountRolesDao;
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.dao.StudentLinkDao;
import com.kh.khedu.dto.AccountDto;
import com.kh.khedu.dto.AccountRolesDto;
import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.ChangeStudentRequestVO;
import com.kh.khedu.vo.student.ChangeStudentResponseVO;
import com.kh.khedu.vo.student.StudentDetailResponseVO;
import com.kh.khedu.vo.student.StudentDetailVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO;
import com.kh.khedu.vo.student.StudentListResponseVO;
import com.kh.khedu.vo.student.StudentUpdateRequestVO;
import com.kh.khedu.vo.student.StudentVO;
import com.kh.khedu.vo.studentLink.StudentLinkResponseVO;
import com.kh.khedu.vo.studentLink.StudentLinkVO;

@Service
public class StudentService {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private AccountRolesDao accountRolesDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RandomService randomService;
	@Autowired
	private StudentLinkDao studentLinkDao;
	@Autowired
	private ParentStudentDao parentStudentDao;
	
	//학생 정보 등록
	@Transactional
	public AccountJoinResponseVO joinStudent(StudentJoinRequestVO request) {
		
		// [1] account정보 담기
		AccountRegisterVO accountVO = new AccountRegisterVO();
		
		BeanUtils.copyProperties(request, accountVO);
		
		accountVO.setAccountType(AccountType.STUDENT.getDescription());
		
		// accountService에서 account관련 정보 등록 (비밀번호는 암호화해서)
		int accountNo = accountService.createAccount(accountVO);
		
		// [2] 상담 정보 등록 
		// 상담테이블에서 이름과 핸드폰 번호로 가져오기(map형태나 vo만들어서 나중에 하기)
		//int consultCustommerNo = consultCustomerDao.selectOne(accountVO.getAccountPhone(), accountVO.getAccountName());
		
		// [3] student 등록
		int studentNo = studentDao.sequence();
		
		StudentVO studentVO = StudentVO.builder()
					.studentNo(studentNo)
					.accountNo(accountNo)
					.consultCustomerNo(null)
					.studentSchool(request.getStudentSchool())
					.studentGrade(request.getStudentGrade())
					.studentGender(request.getStudentGender())
					.studentEtc(request.getStudentEtc())
				.build();
		studentDao.insert(studentVO);
		
		// [4] 권한 등록
		int roleNo = RoleType.STUDENT.getRoleNo();
		AccountRolesDto accountRolesDto = AccountRolesDto.builder()
					.accountNo(accountNo)
					.roleNo(roleNo)
				.build();
		accountRolesDao.insert(accountRolesDto);
		
		//[5] 결과 반환
		return AccountJoinResponseVO.builder()
				.accountNo(accountNo)
				.targetNo(studentNo)
				.accountId(accountVO.getAccountId())
				.accountName(accountVO.getAccountName())
				.accountType(accountVO.getAccountType())
				.message("학생 등록 신청이 완료되었습니다. 데스크 승인 후 서비스 이용이 가능합니다.")
			.build();
	}


	//학생 목록
	public List<StudentListResponseVO> getStudentList() {
        // Dao의 메서드 호출
        return studentDao.selectList();
    }
	
	
	//학생 상세정보
	public StudentDetailResponseVO getStudentDetail(int studentNo) {
	    return studentDao.selectDetail(studentNo);
	}
	
	// 학생 정보 수정 로직
    @Transactional // 🌟 둘 다 성공하거나, 하나라도 실패하면 롤백!
    public void updateStudentInfo(StudentUpdateRequestVO requestVO) {
        // 1. Account 정보 변경
        studentDao.updateAccount(requestVO);
        
        // 2. Student 정보 변경
        studentDao.updateStudent(requestVO);
    }
    
    // 특정 학생이 받고 있는 할인 목록 보기
    public List<StudentDiscountVO> getStudentDiscounts(int studentNo) {
        return studentDao.selectStudentDiscounts(studentNo);
    }

    // 특정 학생에게 새로운 할인 부여
    public void addStudentDiscount(int studentNo, int discountNo) {
        // DAO에 넘겨주기 위해 VO에 번호 2개를 포장합니다.
        StudentDiscountVO vo = new StudentDiscountVO();
        vo.setStudentNo(studentNo);
        vo.setDiscountNo(discountNo);
        
        studentDao.insertStudentDiscount(vo);
    }

    // 학생의 할인 혜택 해제
    public void removeStudentDiscount(int studentDiscountNo) {
        studentDao.deleteStudentDiscount(studentDiscountNo);
    }

	
	//학생 정보 조회
	public StudentDetailVO findMyInfo(String accountId) {
		//[1] 계정 존재 여부 검사
		AccountDto accountDto = accountDao.selectOne(accountId);
		if(accountDto == null) 
			throw new TargetNotfoundException();
		
		//[2] 학생이 아니면
		if(!accountDto.getAccountType().equals(AccountType.STUDENT.getDescription())) 
			throw new WhoAreYouException();
		
		//[3] 학생 번호로 부모정보 조회
		//학생 정보 조회
		StudentDto studentDto = studentDao.selectOne(accountDto.getAccountNo());
		StudentDetailVO studentDetailVO = new StudentDetailVO();
		
		//계정 정보 복사
		BeanUtils.copyProperties(accountDto, studentDetailVO);
		//학생 정보 복사
		BeanUtils.copyProperties(studentDto, studentDetailVO);
		
		//학생의 모든 보호자 조회
		List<ParentStudentDetailVO> parents =
				parentStudentDao.selectParentListByStudentNo(studentDto.getStudentNo());
		
		//보호자 목록 설정
		studentDetailVO.setParents(parents);
		
		return studentDetailVO;
	}
	
	//학생 정보 수정(본인)
	@Transactional
	public ChangeStudentResponseVO updateMyInfo(
			ChangeStudentRequestVO request,
			TokenParseResponseVO parseVO
	) {
		//[1] 정보조회 후 없으면 404처리
		int accountNo = parseVO.getAccountNo();
		AccountDto accountDto = accountDao.selectOneByAccountNo(accountNo);
		if(accountDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[2] 학생인지 확인
		StudentDto studentDto = studentDao.selectOne(accountNo);
		
		if(studentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[3] 계정쪽 입력된 값이 있다면, account 수정 // 이름 연락처 생일 수정일
		BeanUtils.copyProperties(request, accountDto);
		accountDao.updateAll(accountDto);
		//[4] 학생쪽 입력된 값이 있다면, student 수정 // 학교, 학년
		BeanUtils.copyProperties(request, studentDto);
		studentDao.updateAll(studentDto);
		
		//[5] 수정된 정보 다시 조회
		AccountDto resultAccount = accountDao.selectOneByAccountNo(accountNo);
		StudentDto resultStudent = studentDao.selectOne(accountNo);
		
		//[6] 필요한 값 세팅
		ChangeStudentResponseVO response = new ChangeStudentResponseVO();
		BeanUtils.copyProperties(resultAccount, response);
		BeanUtils.copyProperties(resultStudent, response);
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
		
		//[2] 학생인지 확인
		StudentDto studentDto = studentDao.selectOne(accountNo);
		
		if(studentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[3] 비밀번호 확인
		return passwordEncoder
			.matches(
				request.getAccountPassword(), //입력된 비번
				accountDto.getAccountPassword() //DB
		);
	}
	
	// 연동 코드 생성
	public StudentLinkResponseVO createStudentLink(TokenParseResponseVO parseVO) {
		
		//[1] 로그인한 계정정보
		int accountNo = parseVO.getAccountNo();
		//[2] accountNo로 학생 조회
		StudentDto studentDto = studentDao.selectOne(accountNo);
		
		if(studentDto == null) {
			throw new TargetNotfoundException();
		}
		
		//[3] 기존 미사용 코드가 있다면 만료 시키기
		studentLinkDao.expireStudentLink(parseVO.getNoType());
		
		//[4] 새로운 코드 생성
		String linkCode = randomService.generateLinkCode();
		
		//[5] 만료시간
		Timestamp expire = Timestamp.valueOf(
			LocalDateTime.now().plusHours(1)
		);
		
		//[6] DB저장
		int studentLinkNo = studentLinkDao.sequenceLink();
				
		StudentLinkVO studentLinkVO = StudentLinkVO.builder()
					.studentLinkNo(studentLinkNo)
					.studentNo(parseVO.getNoType())
					.linkCode(linkCode)
					.linkExpire(expire)
				.build(); 
		
		studentLinkDao.insertStudentLink(studentLinkVO);
		
		return StudentLinkResponseVO.builder()
					.linkCode(linkCode)
					.linkExpire(expire)
				.build();
	}

}
