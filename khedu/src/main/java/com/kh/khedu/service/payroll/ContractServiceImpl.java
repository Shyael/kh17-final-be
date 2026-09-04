package com.kh.khedu.service.payroll;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.util.SignatureEncryptor;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.ContractAddRequestVO;
import com.kh.khedu.vo.payroll.request.ContractChangeConditionRequestVO;
import com.kh.khedu.vo.payroll.request.ContractEmployeeSignRequestVO;
import com.kh.khedu.vo.payroll.request.ContractEmployerSignRequestVO;
import com.kh.khedu.vo.payroll.request.ContractExtendRequestVO;
import com.kh.khedu.vo.payroll.request.ContractSearchRequestVO;
import com.kh.khedu.vo.payroll.request.ContractUpdateDraftRequestVO;
import com.kh.khedu.vo.payroll.response.ContractAddResponseVO;
import com.kh.khedu.vo.payroll.response.ContractChangeConditionResponseVO;
import com.kh.khedu.vo.payroll.response.ContractDetailResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeDeskResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeTeacherResponseVO;
import com.kh.khedu.vo.payroll.response.ContractExtendResponseVO;
import com.kh.khedu.vo.payroll.response.ContractHistoryResponseVO;
import com.kh.khedu.vo.payroll.response.ContractSearchResponseVO;
import com.kh.khedu.vo.payroll.response.ContractSignDetailResponseVO;
import com.kh.khedu.vo.payroll.response.ContractSignResponseVO;
import com.kh.khedu.vo.payroll.response.ContractUpdateDraftResponseVO;

@Service
public class ContractServiceImpl implements ContractService {

	 @Autowired
	    private ContractDao contractDao;
	 
	    @Autowired
	    private SignatureEncryptor signatureEncryptor;

	    @Autowired
	    private ContractAuthorizationService contractAuthorizationService;

	    @Autowired
	    private ContractPersonInfoService contractPersonInfoService;

	   @Autowired
	   private EmployeeDao employeeDao;

	    private void validateWrittenBreakTimes(
	            double dailyWorkHours,
	            double writtenBreakTimes) {

	        

	        if (writtenBreakTimes < 0) {
	            throw new GetOutException();
	        }

	        if (dailyWorkHours >= 8 && writtenBreakTimes < 60) {
	            throw new GetOutException();
	        }

	        if (dailyWorkHours >= 4
	                && dailyWorkHours < 8
	                && writtenBreakTimes < 30) {
	            throw new GetOutException();
	        }
	    }
	    
	    
	    // 계약 대상 데스크 직원 인적사항 조회
	    @Override
	    public ContractEmployeeDeskResponseVO findDeskPersonInfo(
	            int employeeNo,
	            TokenParseResponseVO parseVO) {

	        boolean isAdmin =
	                contractAuthorizationService.checkAdmin(parseVO);

	        if(!isAdmin)
	            throw new GetOutException();
	        
	        

	        return contractPersonInfoService.findDesk(employeeNo);
	    }


	    // 계약 대상 강사 직원 인적사항 조회
	    @Override
	    public ContractEmployeeTeacherResponseVO findTeacherPersonInfo(
	            int employeeNo,
	            TokenParseResponseVO parseVO) {

	        boolean isAdmin =
	                contractAuthorizationService.checkAdmin(parseVO);

	        if(!isAdmin)
	            throw new GetOutException();

	        return contractPersonInfoService.findTeacher(employeeNo);
	    }

	
	// 단순 조회 

	@Override
	public ContractDetailResponseVO find(long contractNo, TokenParseResponseVO parseVO) {
		ContractDto find = contractDao.find(contractNo);
		
		 if(find == null)
	            throw new TargetNotfoundException();
		
		 boolean valid =
			        contractAuthorizationService
			                .checkAdminOrPartyBOrDeskByContract(
			                        parseVO,
			                        contractNo
			                );

			if (!valid)
			    throw new GetOutException();
		else
			{
			
			ContractDetailResponseVO response = ContractDetailResponseVO.builder()
					.contractNo(find.getContractNo())
		            .employeeNo(find.getEmployeeNo())
		            .wageType(find.getWageType())
		            .baseWage(find.getBaseWage())
		            .dailyWorkHours(find.getDailyWorkHours())
		            .weeklyWorkHours(find.getWeeklyWorkHours())
		            .contractStart(find.getContractStart())
		            .contractEnd(find.getContractEnd())
		            .payday(find.getPayday())
		            .contractContent(find.getContractContent())
		            .contractStatus(find.getContractStatus())
		            .employeeSigned(find.getEmployeeSignature() != null)
		            .employerSigned(find.getEmployerSignature() != null)
		            .signedTime(find.getSignedTime())
		            .writtenBreakMinutes(find.getWrittenBreakMinutes())
		            .build();
			return response;}

	}

	// 근로계약 등록 //권한 설정 완
	@Transactional
	@Override
	public ContractAddResponseVO add(ContractAddRequestVO request, TokenParseResponseVO parseVO) {

		// 직원 상태 조회
	    String employeeStatus =
	            employeeDao.findEmployeeStatus(
	                    request.getEmployeeNo()
	            );

	    if (employeeStatus == null)
	        throw new TargetNotfoundException();


	    // 최초 계약 대상은 대기 직원
	    if (!"대기".equals(employeeStatus))
	        throw new GetOutException();


	    // 권한은 원장만
	    if (!contractAuthorizationService
	            .checkAdmin(parseVO))
	        throw new GetOutException();


	    // 진행중인 계약 확인
	    ContractDto openContract =
	            contractDao.findOpenContract(
	                    request.getEmployeeNo()
	            );


	    // pending / scheduled / active 중
	    // 하나라도 있으면 신규 작성 불가
	    if (openContract != null)
	        throw new GetOutException();
		
		

		// [3] 요청정보를 근로계약 DTO로 변환
		ContractDto contractDto = new ContractDto();
		BeanUtils.copyProperties(request, contractDto);

		// [4] 계약기간 확인
		if (contractDto.getContractEnd() != null
				&& contractDto.getContractEnd().before(contractDto.getContractStart())) {
			throw new GetOutException();
		}

		// [5] 소정근로시간 확인
		if (contractDto.getDailyWorkHours() > contractDto.getWeeklyWorkHours()) {
			throw new GetOutException();
		}
		
		validateWrittenBreakTimes(contractDto.getDailyWorkHours(), contractDto.getWrittenBreakMinutes());

		// [6] 최초 등록 상태는 서명대기
		contractDto.setContractStatus("pending");

		// [7] 근로계약 번호 생성
		long contractNo = contractDao.contractSequence();

		contractDto.setContractNo(contractNo);

		// [8] 신규 계약은 서명 전 상태로 생성
		contractDto.setEmployeeSignature(null);
		contractDto.setEmployerSignature(null);
		contractDto.setSignedTime(null);

		// [9] 신규 근로계약 등록
		contractDao.contractAdd(contractDto);

		
		
		ContractAddResponseVO response =ContractAddResponseVO.builder()
                .contractNo(contractDto.getContractNo())
                .employeeNo(contractDto.getEmployeeNo())
                .wageType(contractDto.getWageType())
                .baseWage(contractDto.getBaseWage())
                .writtenBreakMinutes(contractDto.getWrittenBreakMinutes())
                .dailyWorkHours(contractDto.getDailyWorkHours())
                .weeklyWorkHours(contractDto.getWeeklyWorkHours())
                .contractStart(contractDto.getContractStart())
                .contractEnd(contractDto.getContractEnd())
                .payday(contractDto.getPayday())
                .contractContent(contractDto.getContractContent())
                .contractStatus(contractDto.getContractStatus())
                .build();
		
		return response;
	}

	// 양측 서명 완료 전 근로계약 내용 수정 //권한 설정 완
	@Transactional
	@Override
	public ContractUpdateDraftResponseVO updateDraft (long contractNo, ContractUpdateDraftRequestVO request, TokenParseResponseVO parseVO) {

		boolean valid = contractAuthorizationService.checkAdminOrPartyB(parseVO, contractNo);
		if (!valid)
			throw new GetOutException();
		// [1] 계약 조회
		ContractDto currentContract = contractDao.find(contractNo);

		if (currentContract == null)
			throw new TargetNotfoundException();

		

		// [2] 양측 서명이 모두 작성되었거나
		// 이미 체결완료된 계약이면 수정 불가
		if ((currentContract.getEmployeeSignature() != null && currentContract.getEmployerSignature() != null)
				|| currentContract.getSignedTime() != null) {
			throw new GetOutException();
		}

		// [3] 아직 체결 전 계약만 수정 가능
		if (!"pending".equals(currentContract.getContractStatus()))
			throw new GetOutException();

		// [4] 요청정보 적용
		BeanUtils.copyProperties(request, currentContract);

		// [5] 계약기간 확인
		if (currentContract.getContractEnd() != null
				&& currentContract.getContractEnd().before(currentContract.getContractStart())) {
			throw new GetOutException();
		}
		// [7] 새 소정근로시간 확인
		if (currentContract.getDailyWorkHours() > currentContract.getWeeklyWorkHours()) {
			throw new GetOutException();
		}
		
		validateWrittenBreakTimes(currentContract.getDailyWorkHours(), currentContract.getWrittenBreakMinutes());
		
		request.setContractNo(contractNo);
		
		// [6] 계약내용 수정
		contractDao.updateDraft(request);

		ContractUpdateDraftResponseVO response = ContractUpdateDraftResponseVO.builder()
                .contractNo(currentContract.getContractNo())
                .wageType(currentContract.getWageType())
                .baseWage(currentContract.getBaseWage())
                .dailyWorkHours(currentContract.getDailyWorkHours())
                .weeklyWorkHours(currentContract.getWeeklyWorkHours())
                .writtenBreakMinutes(currentContract.getWrittenBreakMinutes())
                .contractStart(currentContract.getContractStart())
                .contractEnd(currentContract.getContractEnd())
                .payday(currentContract.getPayday())
                .contractContent(currentContract.getContractContent())
                .contractStatus(currentContract.getContractStatus())
                .build();
		return response;
	}

	// 서명 전 작성중인 정보 조회

	@Override
	public ContractSignDetailResponseVO recallBefore(long contractNo, TokenParseResponseVO parseVO) {

		ContractDto contract = contractDao.find(contractNo);

		if (contract == null)
			throw new TargetNotfoundException();

		boolean valid = contractAuthorizationService.checkAdminOrPartyB(parseVO, contractNo);
		if (!valid)
			throw new GetOutException();

		ContractSignDetailResponseVO response = ContractSignDetailResponseVO.builder().contractNo(contract.getContractNo())
				.wageType(contract.getWageType()).baseWage(contract.getBaseWage())
				.dailyWorkHours(contract.getDailyWorkHours()).weeklyWorkHours(contract.getWeeklyWorkHours())
				.contractStart(contract.getContractStart()).contractEnd(contract.getContractEnd())
				.payday(contract.getPayday()).writtenBreakMinutes(contract.getWrittenBreakMinutes())
				.contractContent(contract.getContractContent())
				.contractStatus(contract.getContractStatus()).signedTime(contract.getSignedTime()).build();
		 		
		return response;
	}

	// 을(직원) 서명 //권한 설정 완
	@Transactional
	@Override
	public void employeeSign(long contractNo, ContractEmployeeSignRequestVO request, TokenParseResponseVO parseVO) {

		// 원장 내보내기
		boolean isAdmin = contractAuthorizationService.checkAdmin(parseVO);
		if (isAdmin)
			throw new GetOutException();

		// "을"을 검증
		boolean isPartyB = contractAuthorizationService.checkPartyB(parseVO, contractNo);
		
		
		if (!isPartyB)
			throw new GetOutException();

		// [1] 서명정보 조회
		ContractDto currentContract = contractDao.findSignature(contractNo);

		if (currentContract == null)
			throw new TargetNotfoundException();

		// [2] 이미 양측 서명이 완료된 계약이면 수정 불가
		if (currentContract.getSignedTime() != null)
			throw new GetOutException();

		if (currentContract.getEmployeeSignature() != null)
			throw new GetOutException();


		// [3] 직원 서명 설정
		currentContract.setEmployeeSignature(request.getEmployeeSignature());

		// [4] 직원 서명 저장
		// 암호화는 DAO에서 처리
		boolean result = contractDao.employeeSign(currentContract);

		if (result == false)
			throw new GetOutException();

		// [5] 원장 서명이 이미 존재하면
		// 이번 직원 서명으로 양측 서명 완료
		if (
			    currentContract.getEmployeeSignature() != null
			    &&
			    currentContract.getEmployerSignature() != null
			) {

		    Timestamp now =
		            Timestamp.valueOf(
		                    LocalDateTime.now()
		            );

		    currentContract.setSignedTime(now);


		    LocalDate today =
		            now.toLocalDateTime()
		                    .toLocalDate();

		    LocalDate contractStart =
		            currentContract
		                    .getContractStart()
		                    .toLocalDateTime()
		                    .toLocalDate();


		    // 계약 시작일이 미래면 예약 상태
		    if (contractStart.isAfter(today)) {

		        currentContract.setContractStatus(
		                "scheduled"
		        );

		    }
		    // 당일 또는 이미 시작된 계약
		    else {

		        currentContract.setContractStatus(
		                "active"
		        );
		    }


		    boolean completeResult =
		            contractDao.completeSign(
		                    currentContract
		            );

		    if (!completeResult)
		        throw new GetOutException();


		 // 실제 활성 계약일 때만 직원/계정 활성화
		    if ("active".equals(currentContract.getContractStatus())) {

		        employeeDao.changeUnassignedToWorking(
		                currentContract.getEmployeeNo()
		        );

		        employeeDao.changeAccountStatusToY(
		                currentContract.getEmployeeNo()
		        );
		    }
		}
	}

	// 갑(원장) 서명 //권한 설정 완
	@Transactional
	@Override
	public void employerSign(long contractNo, ContractEmployerSignRequestVO request, TokenParseResponseVO parseVO) {

		boolean isAdmin = contractAuthorizationService.checkAdmin(parseVO);

		if (!isAdmin)
			throw new GetOutException();

		// [1] 서명정보 조회
		ContractDto currentContract = contractDao.findSignature(contractNo);

		if (currentContract == null)
			throw new TargetNotfoundException();

		if (currentContract.getEmployeeSignature() != null)
			throw new GetOutException();


		// [2] 이미 양측 서명이 완료된 계약이면 수정 불가
		if (currentContract.getSignedTime() != null)
			throw new GetOutException();


		// [3] 원장 서명 설정
		currentContract.setEmployerSignature(request.getEmployerSignature());

		// [4] 원장 서명 저장
		// 암호화는 DAO에서 처리
		boolean result = contractDao.employerSign(currentContract);

		if (result == false)
			throw new GetOutException();

		// [5] 직원 서명이 이미 존재하면
		// 이번 원장 서명으로 양측 서명 완료
		if (
			    currentContract.getEmployeeSignature() != null
			    &&
			    currentContract.getEmployerSignature() != null
			) {

		    Timestamp now =
		            Timestamp.valueOf(
		                    LocalDateTime.now()
		            );

		    currentContract.setSignedTime(now);


		    LocalDate today =
		            now.toLocalDateTime()
		                    .toLocalDate();

		    LocalDate contractStart =
		            currentContract
		                    .getContractStart()
		                    .toLocalDateTime()
		                    .toLocalDate();


		    // 미래 시작 계약
		    if (contractStart.isAfter(today)) {

		        currentContract.setContractStatus(
		                "scheduled"
		        );

		    }
		    // 당일 또는 이미 시작
		    else {

		        currentContract.setContractStatus(
		                "active"
		        );
		    }


		    boolean completeResult =
		            contractDao.completeSign(
		                    currentContract
		            );

		    if (!completeResult)
		        throw new GetOutException();


		 // 실제 활성 계약일 때만 직원/계정 활성화
		    if ("active".equals(currentContract.getContractStatus())) {

		        employeeDao.changeUnassignedToWorking(
		                currentContract.getEmployeeNo()
		        );

		        employeeDao.changeAccountStatusToY(
		                currentContract.getEmployeeNo()
		        );
		    }
		}
	}
	

	// 현재 근로계약 조회
	@Override
	public ContractDetailResponseVO findCurrent(int employeeNo, TokenParseResponseVO parseVO) {

		boolean hasPermission = contractAuthorizationService.checkAdminOrPartyBOrDeskByEmployee(parseVO, employeeNo);

		if (!hasPermission)
			throw new GetOutException();

		ContractDto contractDto = contractDao.findCurrent(employeeNo);

		if (contractDto == null)
			throw new TargetNotfoundException();

		ContractDetailResponseVO response = ContractDetailResponseVO.builder()
				.contractNo(contractDto.getContractNo())
	            .employeeNo(contractDto.getEmployeeNo())
	            .wageType(contractDto.getWageType())
	            .baseWage(contractDto.getBaseWage())
	            .dailyWorkHours(contractDto.getDailyWorkHours())
	            .weeklyWorkHours(contractDto.getWeeklyWorkHours())
	            .contractStart(contractDto.getContractStart())
	            .contractEnd(contractDto.getContractEnd())
	            .payday(contractDto.getPayday())
	            .contractContent(contractDto.getContractContent())
	            .contractStatus(contractDto.getContractStatus())
	            .employeeSigned(contractDto.getEmployeeSignature() != null)
	            .employerSigned(contractDto.getEmployerSignature() != null)
	            .signedTime(contractDto.getSignedTime())
	            .writtenBreakMinutes(contractDto.getWrittenBreakMinutes())
	            .build();
		return response;
	}

	// 과거 근로계약 조회
	@Override
	public List<ContractHistoryResponseVO> findPast(int employeeNo, TokenParseResponseVO parseVO) {
		// 권한은 당사자, 데스크, 원장만
		
		
		boolean hasPermission = contractAuthorizationService.checkAdminOrPartyBOrDeskByEmployee(parseVO, employeeNo);
		if(!hasPermission) throw new GetOutException();
		
		List<ContractDto> history = contractDao.findPast(employeeNo);

		if (history.size() == 0)
			throw new TargetNotfoundException();
		
		List<ContractHistoryResponseVO> response =
		        history.stream()
		                .map(contractDto ->
		                        ContractHistoryResponseVO.builder()
		                                .contractNo(contractDto.getContractNo())
		                                .employeeNo(contractDto.getEmployeeNo())
		                                .wageType(contractDto.getWageType())
		                                .baseWage(contractDto.getBaseWage())
		                                .contractStart(contractDto.getContractStart())
		                                .contractEnd(contractDto.getContractEnd())
		                                .contractStatus(contractDto.getContractStatus())
		                                .signedTime(contractDto.getSignedTime())
		                                .build()
		                )
		                .toList();
		
		
		return response;
	}

	// 직원의 전체 근로계약 조회
	@Override
	public List<ContractHistoryResponseVO> findAllByEmployee(int employeeNo, TokenParseResponseVO parseVO) {

		boolean hasPermission =
		        contractAuthorizationService
		                .checkAdminOrPartyBOrDeskByEmployee(
		                        parseVO,
		                        employeeNo
		                );
		if(!hasPermission) throw new GetOutException();
		
		List<ContractDto> history =
		        contractDao.findAllByEmployee(employeeNo);

		if (history.size() == 0)
			throw new TargetNotfoundException();
		
		 List<ContractHistoryResponseVO> response =
		            history.stream()
		                    .map(contractDto ->
		                            ContractHistoryResponseVO.builder()
		                                    .contractNo(
		                                            contractDto.getContractNo()
		                                    )
		                                    .employeeNo(
		                                            contractDto.getEmployeeNo()
		                                    )
		                                    .wageType(
		                                            contractDto.getWageType()
		                                    )
		                                    .baseWage(
		                                            contractDto.getBaseWage()
		                                    )
		                                    .contractStart(
		                                            contractDto.getContractStart()
		                                    )
		                                    .contractEnd(
		                                            contractDto.getContractEnd()
		                                    )
		                                    .contractStatus(
		                                            contractDto.getContractStatus()
		                                    )
		                                    .signedTime(
		                                            contractDto.getSignedTime()
		                                    )
		                                    .build()
		                    )
		                    .toList();


		    return response;
	}

	// 근로계약 연장(기간만) //검수 필
	@Transactional
	@Override
	public ContractExtendResponseVO extendContract(ContractExtendRequestVO request, TokenParseResponseVO parseVO) {

		//권한은 원장만
		boolean isAdmin = contractAuthorizationService.checkAdmin(parseVO);
		
		if(!isAdmin) throw new GetOutException();
		
		 // [2] 기존 계약 조회
	    ContractDto contractDto =
	            contractDao.findPeriodAndStatus(
	                    request.getContractNo()
	            );

	    if(contractDto == null)
	        throw new TargetNotfoundException();


	    // [3] 종료된 계약은 연장 불가
	    if("ended".equals(
	            contractDto.getContractStatus()
	    ))
	        throw new GetOutException();


	    // [4] 기간의 정함이 없는 계약은 연장 불가
	    if(contractDto.getContractEnd() == null)
	        throw new GetOutException();


	    // [5] 새 종료일은 기존 종료일보다 뒤여야 함
	    if(
	        !request.getContractEnd()
	                .after(contractDto.getContractEnd())
	    )
	        throw new GetOutException();


	    // [6] DTO에 새 종료일 반영
	    contractDto.setContractEnd(
	            request.getContractEnd()
	    );


	    // [7] DB 수정
	    boolean result =
	            contractDao.extendContract(
	                    contractDto
	            );

	    if(result == false)
	        throw new GetOutException();


	    // [8] 응답 조립
	    ContractExtendResponseVO response = ContractExtendResponseVO.builder()
	            .contractNo(
	                    contractDto.getContractNo()
	            )
	            .contractStart(
	                    contractDto.getContractStart()
	            )
	            .contractEnd(
	                    contractDto.getContractEnd()
	            )
	            .contractStatus(
	                    contractDto.getContractStatus()
	            )
	            .build();
	    
	    return response;
	}

	// 체결 후 근로조건 변경
	@Transactional
	@Override
	public ContractChangeConditionResponseVO changeWorkCondition(
	        long contractNo,
	        ContractChangeConditionRequestVO request,
	        TokenParseResponseVO parseVO) {


	    // [1] 권한은 원장만
	    boolean isAdmin =
	            contractAuthorizationService
	                    .checkAdmin(parseVO);

	    if (!isAdmin)
	        throw new GetOutException();


	    // [2] 기존 계약 조회
	    ContractDto originDto =
	            contractDao.find(contractNo);

	    if (originDto == null)
	        throw new TargetNotfoundException();


	    // [3] 체결 완료 계약인지 확인
	    if (originDto.getSignedTime() == null)
	        throw new GetOutException();


	    // [4] 이미 종료된 계약은 변경 불가
	    if ("ended".equals(
	            originDto.getContractStatus()
	    ))
	        throw new GetOutException();

	    
	 // 이미 다른 진행중 계약이 있는지 확인
	    ContractDto openContract =
	            contractDao.findOpenContract(
	                    originDto.getEmployeeNo()
	            );


	    // 현재 변경 대상 계약이 아닌
	    // pending / scheduled / active 계약이 존재하면
	    // 추가 근로조건 변경 불가
	    if (
	        openContract != null
	        &&
	        openContract.getContractNo()
	            != originDto.getContractNo()
	    ) {
	        throw new GetOutException();
	    }


	    // [5] 새 계약 생성
	    ContractDto newContractDto =
	            new ContractDto();

	    BeanUtils.copyProperties(
	            request,
	            newContractDto
	    );


	    // 직원은 기존 계약의 직원 그대로
	    newContractDto.setEmployeeNo(
	            originDto.getEmployeeNo()
	    );



	    // [6] 새 계약기간 확인
	    if (
	        newContractDto.getContractEnd() != null
	        &&
	        newContractDto
	                .getContractEnd()
	                .before(
	                    newContractDto.getContractStart()
	                )
	    ) {

	        throw new GetOutException();
	    }



	    // [7] 소정근로시간 확인
	    if (
	        newContractDto.getDailyWorkHours()
	        >
	        newContractDto.getWeeklyWorkHours()
	    ) {

	        throw new GetOutException();
	    }


	    validateWrittenBreakTimes(
	            newContractDto.getDailyWorkHours(),
	            newContractDto.getWrittenBreakMinutes()
	    );



	    // [8] 근로조건 변경은 미래부터 적용
	    Timestamp current =
	            Timestamp.valueOf(
	                    LocalDateTime.now()
	            );

	    if (
	        !newContractDto
	                .getContractStart()
	                .after(current)
	    ) {

	        throw new GetOutException();
	    }



	    // [9] 기존 계약은 새 계약 시작 시점에 종료
	    originDto.setContractEnd(
	    	    newContractDto.getContractStart()
	    	);

	    	boolean closeResult =
	    	        contractDao.closeForConditionChange(
	    	                originDto
	    	        );

	    	if (!closeResult)
	    	    throw new GetOutException();


	    // [10] 새 계약은 다시 서명대기
	    newContractDto.setContractStatus(
	            "pending"
	    );


	    // [11] 새 계약번호 생성
	    long newContractNo =
	            contractDao.contractSequence();

	    newContractDto.setContractNo(
	            newContractNo
	    );


	    // [12] 서명 초기화
	    newContractDto.setEmployeeSignature(
	            null
	    );

	    newContractDto.setEmployerSignature(
	            null
	    );

	    newContractDto.setSignedTime(
	            null
	    );


	    // [13] 새 계약 등록
	    contractDao.contractAdd(
	            newContractDto
	    );



	    // [14] 응답
	    ContractChangeConditionResponseVO response =
	            ContractChangeConditionResponseVO
	                    .builder()

	                    .contractNo(
	                        newContractDto.getContractNo()
	                    )

	                    .employeeNo(
	                        newContractDto.getEmployeeNo()
	                    )

	                    .wageType(
	                        newContractDto.getWageType()
	                    )

	                    .baseWage(
	                        newContractDto.getBaseWage()
	                    )

	                    .dailyWorkHours(
	                        newContractDto.getDailyWorkHours()
	                    )

	                    .weeklyWorkHours(
	                        newContractDto.getWeeklyWorkHours()
	                    )

	                    .contractStart(
	                        newContractDto.getContractStart()
	                    )

	                    .contractEnd(
	                        newContractDto.getContractEnd()
	                    )

	                    .payday(
	                        newContractDto.getPayday()
	                    )

	                    .contractContent(
	                        newContractDto.getContractContent()
	                    )

	                    .contractStatus(
	                        newContractDto.getContractStatus()
	                    )

	                    .writtenBreakMinutes(
	                        newContractDto
	                            .getWrittenBreakMinutes()
	                    )

	                    .build();


	    return response;
	}

	// 서명정보 조회
	@Override
	public ContractSignResponseVO findSignature(long contractNo, TokenParseResponseVO parseVO) {

		ContractDto find = contractDao.findSignature(contractNo);

		if (find == null)
			throw new TargetNotfoundException();

		// 권한은 당사자, 데스크, 원장만
		
		boolean hasPermission = contractAuthorizationService.checkAdminOrPartyBOrDeskByContract(parseVO, contractNo);

		if(!hasPermission) throw new GetOutException();
		
		
		ContractDto target = ContractDto.builder()
				.employeeSignature(signatureEncryptor.decrypt(find.getEmployeeSignature()))
				.employerSignature(signatureEncryptor.decrypt(find.getEmployerSignature()))
				.build();
				
		ContractSignResponseVO response = ContractSignResponseVO.builder()
				.employeeSignature(target.getEmployeeSignature())
				.employerSignature(target.getEmployerSignature())
				.build();
		return response;
	}

	// 계약기간에 따른 상태 갱신
	@Transactional
	@Override
	public void refreshContractStatus() {

	    // 종료일 도래 계약 종료
	    contractDao.endContracts();

	    // 시작일 도래 + 서명완료 계약 활성화
	    contractDao.activateContracts();

	    // active 계약을 가진 대기 직원 재직 처리
	    employeeDao.activateWaitingEmployees();

	    // 재직 + active 계약 직원 계정 활성화
	    employeeDao.activateEmployeeAccounts();
	}

	@Transactional
	@Override
	public void exitContract(
	        long contractNo,
	        TokenParseResponseVO parseVO) {

	    boolean isAdmin =
	            contractAuthorizationService.checkAdmin(
	                    parseVO
	            );

	    if (!isAdmin)
	        throw new GetOutException();


	    ContractDto find =
	            contractDao.find(
	                    contractNo
	            );

	    if (find == null)
	        throw new TargetNotfoundException();


	    if ("ended".equals(
	            find.getContractStatus()
	    ))
	        throw new GetOutException();


	    boolean result =
	            contractDao.exitContracts(
	                    contractNo
	            );

	    if (!result)
	        throw new GetOutException();
	}


	
	@Override
	public List<ContractSearchResponseVO> contractSearch(
	        ContractSearchRequestVO request,
	        TokenParseResponseVO parseVO) {

	    boolean isAdmin =
	            contractAuthorizationService
	                    .checkAdmin(parseVO);

	    if (!isAdmin)
	        throw new GetOutException();


	    List<ContractSearchResponseVO> result =
	            contractDao.contractSearch(
	                    request
	            );


	    if (result.size() == 0)
	        throw new TargetNotfoundException();


	    return result;
	}
	
}