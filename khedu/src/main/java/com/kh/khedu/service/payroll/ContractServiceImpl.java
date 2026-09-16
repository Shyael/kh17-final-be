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
import com.kh.khedu.error.AdminChecker;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.YouAreNotAdminException;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.util.SignatureEncryptor;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.ContractAddRequestVO;
import com.kh.khedu.vo.payroll.request.ContractChangeConditionRequestVO;
import com.kh.khedu.vo.payroll.request.ContractEmployeeSignRequestVO;
import com.kh.khedu.vo.payroll.request.ContractEmployerSignRequestVO;
import com.kh.khedu.vo.payroll.request.ContractExtendRequestVO;
import com.kh.khedu.vo.payroll.request.ContractListSearchVO;
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
		private AdminChecker adminChecker;

	   @Autowired
	   private EmployeeDao employeeDao;

    // 일반 성인·일 8시간/주 40시간제, 매주 동일한 소정근로시간 기준.
    private static final double MINIMUM_HOURLY_WAGE_2026 = 10320;

    private void validateWrittenBreakTimes(
            double dailyWorkHours, double weeklyWorkHours, double writtenBreakTimes) {
        if (!Double.isFinite(dailyWorkHours) || !Double.isFinite(weeklyWorkHours)
                || dailyWorkHours <= 0 || weeklyWorkHours <= 0
                || dailyWorkHours > 8 || weeklyWorkHours > 40
                || dailyWorkHours > weeklyWorkHours
                || !Double.isFinite(writtenBreakTimes) || writtenBreakTimes < 0
                || writtenBreakTimes != Math.floor(writtenBreakTimes)) {
            throw new GetOutException();
        }
        double minimumBreakMinutes = dailyWorkHours >= 8 ? 60 : dailyWorkHours >= 4 ? 30 : 0;
        if (writtenBreakTimes < minimumBreakMinutes) {
            throw new GetOutException();
        }
    }

    private void validateContractTerms(ContractDto contractDto) {
        // Number 변수로 받아 primitive/wrapper 숫자 타입 모두 처리하고 null을 먼저 확인한다.
        Number daily = contractDto.getDailyWorkHours();
        Number weekly = contractDto.getWeeklyWorkHours();
        Number breakMinutes = contractDto.getWrittenBreakMinutes();
        Number wage = contractDto.getBaseWage();
        Number payday = contractDto.getPayday();
        if (daily == null || weekly == null || breakMinutes == null || wage == null
                || payday == null || contractDto.getContractStart() == null) {
            throw new GetOutException();
        }
        double dailyWorkHours = daily.doubleValue();
        double weeklyWorkHours = weekly.doubleValue();
        double baseWage = wage.doubleValue();
        validateWrittenBreakTimes(dailyWorkHours, weeklyWorkHours, breakMinutes.doubleValue());
        String wageType = contractDto.getWageType();
        if ((!"hourly".equals(wageType) && !"daily".equals(wageType) && !"monthly".equals(wageType))
                || !Double.isFinite(baseWage) || baseWage <= 0) {
            throw new GetOutException();
        }
        if (!Double.isFinite(payday.doubleValue()) || payday.doubleValue() < 1
                || payday.doubleValue() > 31 || payday.doubleValue() != Math.floor(payday.doubleValue())) {
            throw new GetOutException();
        }
        if (contractDto.getContractEnd() != null
                && contractDto.getContractEnd().before(contractDto.getContractStart())) {
            throw new GetOutException();
        }
        if (weeklyWorkHours < 15) {
            // 프로젝트 정책: 법정 주휴일 비대상은 null로 저장.
            contractDto.setWeeklyHolidayDay(null);
        } else {
            String holiday = contractDto.getWeeklyHolidayDay();
            if (holiday == null || !List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                    "FRIDAY", "SATURDAY", "SUNDAY").contains(holiday)) {
                throw new GetOutException();
            }
        }
        LocalDate start = contractDto.getContractStart().toLocalDateTime().toLocalDate();
        LocalDate end = contractDto.getContractEnd() == null ? null
                : contractDto.getContractEnd().toLocalDateTime().toLocalDate();
        boolean covers2026 = !start.isAfter(LocalDate.of(2026, 12, 31))
                && (end == null || !end.isBefore(LocalDate.of(2026, 1, 1)));
        if (covers2026) {
            // 기본임금만으로 충족시키는 정책. 가산수당/별도 수당을 합산하지 않는다.
            // 통상근로자 주 5일, 추가 약정 유급시간 없음. 주 40시간은 월 209시간.
            // 단시간 근로자의 월 환산시간은 중간 반올림 없이 계산한다.
            double weeklyPaidHolidayHours = weeklyWorkHours >= 15 ? weeklyWorkHours / 5 : 0;
            double monthlyHours = weeklyWorkHours == 40 ? 209
                    : (weeklyWorkHours + weeklyPaidHolidayHours) * 365 / 7 / 12;
            double wageHours = "hourly".equals(wageType) ? 1
                    : "daily".equals(wageType) ? dailyWorkHours : monthlyHours;
            double minimumWage = Math.ceil(MINIMUM_HOURLY_WAGE_2026 * wageHours);
            if (baseWage < minimumWage) {
                throw new GetOutException();
            }
        }
        // 다른 연도의 최저임금은 해당 연도 기준을 별도로 추가해야 한다.
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
		            .weeklyWorkHours(find.getWeeklyWorkHours())
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


	    //계약 대상은 대기 혹은 종료 직원
	    if (!"대기".equals(employeeStatus) && !"종료".equals(employeeStatus))
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

        validateContractTerms(contractDto);

		// [6] 신규 등록 상태는 서명대기
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
                .weelkyHolidayDay(contractDto.getWeeklyHolidayDay())
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

        validateContractTerms(currentContract);

        // 검증에서 정규화한 주휴일 null을 실제 DAO 저장 요청에도 반영한다.
        BeanUtils.copyProperties(currentContract, request);
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
                .weeklyHolidayDay(currentContract.getWeeklyHolidayDay())
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
				.contractStatus(contract.getContractStatus()).signedTime(contract.getSignedTime())
				.weeklyHolidayDay(contract.getWeeklyHolidayDay()!=null ? contract.getWeeklyHolidayDay() : null)
				.build();
		 		
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

		if (currentContract.getEmployerSignature() != null)
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
	            .weeklyHolidayDay(contractDto.getWeeklyHolidayDay() !=null ? contractDto.getWeeklyHolidayDay() : null)
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
		                                .weeklyHolidayDay(contractDto.getWeeklyHolidayDay())
		                                .build()
		                )
		                .toList();
		
		
		return response;
	}

	// 직원의 전체 근로계약 조회
	// 직원의 전체 근로계약 조회
	@Override
	public List<ContractHistoryResponseVO> findAllByEmployee(
	        int employeeNo,
	        TokenParseResponseVO parseVO) {

	    boolean hasPermission =
	            contractAuthorizationService
	                    .checkAdminOrPartyBOrDeskByEmployee(
	                            parseVO,
	                            employeeNo
	                    );

	    if (!hasPermission)
	        throw new GetOutException();


	    List<ContractDto> history =
	            contractDao.findAllByEmployee(
	                    employeeNo
	            );


	    List<ContractHistoryResponseVO> response =
	            history.stream()
	                    .map(contractDto ->
	                            ContractHistoryResponseVO
	                                    .builder()

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

	                                    .weeklyHolidayDay(
	                                            contractDto.getWeeklyHolidayDay()
	                                    )

	                                    .build()
	                    )
	                    .toList();


	    return response;
	}

				
	// 근로계약 연장
	@Transactional
	@Override
	public ContractExtendResponseVO extendContract(
        ContractExtendRequestVO request,
        TokenParseResponseVO parseVO) {

    // [1] 원장만 가능
    boolean isAdmin =
            contractAuthorizationService
                    .checkAdmin(parseVO);

    if (!isAdmin)
        throw new GetOutException();


    // [2] 기존 계약 전체 조회
    ContractDto originDto =
            contractDao.find(
                    request.getContractNo()
            );

    if (originDto == null)
        throw new TargetNotfoundException();


    // [3] 체결 완료 계약만 연장 가능
    if (originDto.getSignedTime() == null)
        throw new GetOutException();


    // [4] 종료 계약 연장 불가
    if (
        "ended".equals(
            originDto.getContractStatus()
        )
    ) {
        throw new GetOutException();
    }


    // [5] 기간의 정함이 없는 계약은 연장 불가
    if (originDto.getContractEnd() == null)
        throw new GetOutException();


    // [6] 새 종료일 확인
    if (
        request.getContractEnd() == null
        ||
        !request
            .getContractEnd()
            .after(
                originDto.getContractEnd()
            )
    ) {
        throw new GetOutException();
    }


    // [7] 이미 다른 진행중 계약 존재 여부
    ContractDto openContract =
            contractDao.findOpenContract(
                    originDto.getEmployeeNo()
            );

    if (
        openContract != null
        &&
        openContract.getContractNo()
        != originDto.getContractNo()
    ) {
        throw new GetOutException();
    }


    // [8] 연장 계약 시작일
    LocalDate extensionStartDate =
            originDto
                    .getContractEnd()
                    .toLocalDateTime()
                    .toLocalDate()
                    .plusDays(1);


    // [9] 새 계약 생성
    ContractDto newContractDto =
            new ContractDto();


    // 기존 계약조건 복사
    BeanUtils.copyProperties(
            originDto,
            newContractDto
    );


    // [10] 새 계약번호
    long newContractNo =
            contractDao.contractSequence();

    newContractDto.setContractNo(
            newContractNo
    );


    // [11] 연장 기간
    newContractDto.setContractStart(
            Timestamp.valueOf(
                extensionStartDate
                    .atStartOfDay()
            )
    );

    newContractDto.setContractEnd(
            request.getContractEnd()
    );


    // [12] 새 계약은 서명 대기
    newContractDto.setContractStatus(
            "pending"
    );


    // [13] 서명 초기화
    newContractDto.setEmployeeSignature(
            null
    );

    newContractDto.setEmployerSignature(
            null
    );

    newContractDto.setSignedTime(
            null
    );


    validateContractTerms(newContractDto);

    // [14] 새 계약 등록
    contractDao.contractAdd(
            newContractDto
    );


    // [15] 응답
    ContractExtendResponseVO response =
            ContractExtendResponseVO
                    .builder()

                    .contractNo(
                        newContractDto
                            .getContractNo()
                    )

                    .contractStart(
                        newContractDto
                            .getContractStart()
                    )

                    .contractEnd(
                        newContractDto
                            .getContractEnd()
                    )

                    .contractStatus(
                        newContractDto
                            .getContractStatus()
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



        validateContractTerms(newContractDto);

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
	    LocalDate previousContractEndDate =
	            newContractDto
	                    .getContractStart()
	                    .toLocalDateTime()
	                    .toLocalDate()
	                    .minusDays(1);

	    originDto.setContractEnd(
	            Timestamp.valueOf(
	                    previousContractEndDate.atStartOfDay()
	            )
	    );

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
	                    
	                    .weeklyHolidayDay(newContractDto.getWeeklyHolidayDay() != null ? newContractDto.getWeeklyHolidayDay() : null)
	                    
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
		boolean hasPermission =
		        contractAuthorizationService.checkAdminOrPartyBOrDeskByContract(parseVO, contractNo);

		if (!hasPermission)
		    throw new GetOutException();

		ContractDto target = ContractDto.builder()
		        .employeeSignature(
		                find.getEmployeeSignature() == null
		                        ? null
		                        : signatureEncryptor.decrypt(find.getEmployeeSignature())
		        )
		        .employerSignature(
		                find.getEmployerSignature() == null
		                        ? null
		                        : signatureEncryptor.decrypt(find.getEmployerSignature())
		        )
		        .build();

		ContractSignResponseVO response = ContractSignResponseVO.builder()
		        .employeeSignature(target.getEmployeeSignature())
		        .employerSignature(target.getEmployerSignature())
		        .build();

		return response;
	}

	@Transactional
	@Override
	public void refreshEndedContractStatus() {

		contractDao.endContracts();

		contractDao.deactivateEndedEmployees();

		contractDao.deactivateEndedEmployeeAccounts();
	}
	
	
	@Transactional
	@Override
	public void refreshActiveContractStatus() {

		// 시작일 도래 + 서명완료 계약 활성화
		contractDao.activateContracts();

		// active 계약을 가진 대기 직원 재직 처리
		contractDao.activateWaitingEmployees();

		// 재직 + active 계약 직원 계정 활성화
		contractDao.activateEmployeeAccounts();
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


		// 후속 계약이 없는 직원 대기 처리
		contractDao.deactivateEndedEmployees();

		// 대기 직원 계정 비활성화
		contractDao.deactivateEndedEmployeeAccounts();
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
	
	
	@Transactional
	@Override
	public void cancelContract(
			long contractNo,
			TokenParseResponseVO parseVO) {

		// =========================
		// 관리자 권한 확인
		// =========================

		boolean isAdmin = adminChecker.AdminCheck(parseVO);
		if(isAdmin == false) throw new YouAreNotAdminException();

		// =========================
		// 계약 조회
		// =========================

		ContractDto contractDto =
				contractDao.find(
						contractNo
				);

		if (contractDto == null)
			throw new TargetNotfoundException();


		// =========================
		// pending 계약만 취소 가능
		// =========================

		if (!"pending".equals(
				contractDto.getContractStatus()
		))
			throw new GetOutException();


		// =========================
		// 서명 완료시간이 존재하면 취소 불가
		// =========================

		if (contractDto.getSignedTime() != null)
			throw new GetOutException();

		
		// =========================
		// 계약 삭제
		// =========================

		boolean result =
				contractDao.cancelContract(
						contractNo
				);
		
		System.out.println(result);

		if (!result)
			throw new GetOutException();
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PageResponseVO<ContractHistoryResponseVO> selectList(
	        ContractListSearchVO search,
	        TokenParseResponseVO parseVO) {


	    // 계약 전체목록은 원장만
	    boolean isAdmin =
	            contractAuthorizationService
	                    .checkAdmin(parseVO);


	    if (isAdmin==false) {
	        throw new GetOutException();
	    }


	    // 현재 페이지 목록
	    List<ContractHistoryResponseVO> list =
	            contractDao.selectSearchList(
	                    search);


	    // 검색조건 전체 개수
	    int totalCount =
	            contractDao.selectCount(
	                    search);


	    return new PageResponseVO<>(
	            list,
	            totalCount,
	            search
	    );
	}
}