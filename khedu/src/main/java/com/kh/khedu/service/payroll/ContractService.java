package com.kh.khedu.service.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.requestvo.payroll.ContractAddRequestVO;
import com.kh.khedu.requestvo.payroll.ContractChangeConditionRequestVO;
import com.kh.khedu.requestvo.payroll.ContractEmployeeSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractEmployerSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractUpdateDraftRequestVO;
import com.kh.khedu.responsevo.payroll.ContractSignResponseVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface ContractService {

	// 근로계약 등록
	ContractDto add(
			ContractAddRequestVO request,TokenParseResponseVO parseVO
	);

	// 양측 서명 완료 전 근로계약 내용 수정
	void updateDraft(
			long contractNo,
			ContractUpdateDraftRequestVO request,
			TokenParseResponseVO parseVO
	);

	// 을(직원) 서명
	void employeeSign(
			long contractNo,
			ContractEmployeeSignRequestVO request,
			TokenParseResponseVO parseVO
	);

	// 갑(원장) 서명
	void employerSign(
			long contractNo,
			ContractEmployerSignRequestVO request,
			TokenParseResponseVO parseVO
	);

	// 현재 근로계약 조회
	ContractDto findCurrent(
			long employeeNo
	);

	// 과거(종료) 근로계약 조회
	List<ContractDto> findPast(
			long employeeNo
	);

	// 직원의 전체 근로계약 조회
	List<ContractDto> findAllByEmployee(
			long employeeNo
	);

	// 체결 후 근로조건 변경
	ContractDto changeWorkCondition(
			long contractNo,
			ContractChangeConditionRequestVO request
	);

	// 근로계약 종료
	void endContract(
			long contractNo
	);

	// 서명정보 조회
	ContractSignResponseVO findSignature(
			long contractNo,
			TokenParseResponseVO parseVO
	);

	// 시작일 도래 계약 활성화 및 종료일 도래 계약 종료
	void refreshContractStatus(long contractNo);
	
	//도중 퇴사
	
	void exitContract(long contractNo);

}