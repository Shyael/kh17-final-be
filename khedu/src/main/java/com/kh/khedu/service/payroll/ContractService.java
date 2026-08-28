package com.kh.khedu.service.payroll;

import java.util.List;

import com.kh.khedu.requestvo.payroll.ContractAddRequestVO;
import com.kh.khedu.requestvo.payroll.ContractChangeConditionRequestVO;
import com.kh.khedu.requestvo.payroll.ContractEmployeeSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractEmployerSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractExtendRequestVO;
import com.kh.khedu.requestvo.payroll.ContractUpdateDraftRequestVO;
import com.kh.khedu.responsevo.payroll.ContractAddResponseVO;
import com.kh.khedu.responsevo.payroll.ContractChangeConditionResponseVO;
import com.kh.khedu.responsevo.payroll.ContractDetailResponseVO;
import com.kh.khedu.responsevo.payroll.ContractExtendResponseVO;
import com.kh.khedu.responsevo.payroll.ContractHistoryResponseVO;
import com.kh.khedu.responsevo.payroll.ContractSignDetailResponseVO;
import com.kh.khedu.responsevo.payroll.ContractSignResponseVO;
import com.kh.khedu.responsevo.payroll.ContractUpdateDraftResponseVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface ContractService {

	//단순 조회
	ContractDetailResponseVO find(long contractNo, TokenParseResponseVO parseVO);
	
	// 근로계약 등록
	ContractAddResponseVO add(
			ContractAddRequestVO request,TokenParseResponseVO parseVO
	);

	// 양측 서명 완료 전 근로계약 내용 수정
	ContractUpdateDraftResponseVO updateDraft(
			long contractNo,
			ContractUpdateDraftRequestVO request,
			TokenParseResponseVO parseVO
	);

	//서명 전 작성 중 정보 불러오기
	
	ContractSignDetailResponseVO recallBefore(long contractNo, TokenParseResponseVO parseVO);
	
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
	ContractDetailResponseVO findCurrent(
			int employeeNo , TokenParseResponseVO parseVO
	);

	// 과거(종료) 근로계약 조회
	List<ContractHistoryResponseVO> findPast(
			int employeeNo, TokenParseResponseVO parseVO
	);

	// 직원의 전체 근로계약 조회
	List<ContractHistoryResponseVO> findAllByEmployee(
			int employeeNo, TokenParseResponseVO parseVO
	);

	// 체결 후 근로조건 변경
	ContractChangeConditionResponseVO changeWorkCondition(
			long contractNo,
			ContractChangeConditionRequestVO request
			,TokenParseResponseVO parseVO
	);

	// 근로계약 연장(기간만)
	ContractExtendResponseVO extendContract(
			ContractExtendRequestVO request,
			TokenParseResponseVO parseVO
	);

	// 서명정보 조회
	ContractSignResponseVO findSignature(
			long contractNo,
			TokenParseResponseVO parseVO
	);

	// 시작일 도래 계약 활성화 및 종료일 도래 계약 종료
	void refreshContractStatus();
	
	//도중 퇴사
	
	void exitContract(long contractNo, TokenParseResponseVO parseVO);

}