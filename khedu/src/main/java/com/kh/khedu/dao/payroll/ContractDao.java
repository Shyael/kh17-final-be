package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.requestvo.payroll.ContractChangeConditionRequestVO;
import com.kh.khedu.requestvo.payroll.ContractUpdateDraftRequestVO;

public interface ContractDao {

	// 근로계약 번호 생성
	long contractSequence();

	// 근로계약 초안 등록
	void contractAdd(ContractDto contractDto);

	// 계약번호로 근로계약 조회
	ContractDto find(long contractNo);

	// 직원의 현재 근로계약 조회
	ContractDto findCurrent(long employeeNo);

	// 직원의 과거(종료) 근로계약 조회
	List<ContractDto> findPast(long employeeNo);

	// 직원의 전체 근로계약 조회
	List<ContractDto> findAllByEmployee(long employeeNo);

	// 계약의 급여조건 조회
	ContractDto findWageCondition(long contractNo);

	// 계약의 근로시간 조건 조회
	ContractDto findWorkTimeCondition(long contractNo);

	// 계약기간 및 계약상태 조회
	ContractDto findPeriodAndStatus(long contractNo);

	// 급여 지급예정일 조회
	Integer findPayday(long contractNo);

	// 계약 서명정보 조회
	ContractDto findSignature(long contractNo);

	//서명 후 근무 조건 계약 내용 수정
	boolean changeContractCondition(ContractChangeConditionRequestVO request);
	
	// 양측 서명 완료 전 계약내용 수정
	boolean updateDraft(ContractUpdateDraftRequestVO request);

	// 을(직원) 서명
	boolean employeeSign(ContractDto contractDto);

	// 갑(원장) 서명
	boolean employerSign(ContractDto contractDto);

	// 양측 서명 완료 처리
	boolean completeSign(ContractDto contractDto);

	//계약 연장(기간만)
	boolean extendContract(ContractDto contractDto);
	
	// 시작일이 도래한 체결완료 계약 활성화
	boolean activateContracts();

	// 종료일이 도래한 계약 종료
	boolean endContracts();
	
	//도중 퇴사
	boolean exitContracts(long contactNo);
	
	
}