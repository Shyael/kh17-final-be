package com.kh.khedu.controller;



import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.payroll.ContractService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.ContractAddRequestVO;
import com.kh.khedu.vo.payroll.request.ContractChangeConditionRequestVO;
import com.kh.khedu.vo.payroll.request.ContractEmployeeSignRequestVO;
import com.kh.khedu.vo.payroll.request.ContractEmployerSignRequestVO;
import com.kh.khedu.vo.payroll.request.ContractExtendRequestVO;
import com.kh.khedu.vo.payroll.request.ContractUpdateDraftRequestVO;
import com.kh.khedu.vo.payroll.response.ContractAddResponseVO;
import com.kh.khedu.vo.payroll.response.ContractChangeConditionResponseVO;
import com.kh.khedu.vo.payroll.response.ContractDetailResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeDeskResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeTeacherResponseVO;
import com.kh.khedu.vo.payroll.response.ContractExtendResponseVO;
import com.kh.khedu.vo.payroll.response.ContractHistoryResponseVO;
import com.kh.khedu.vo.payroll.response.ContractSignDetailResponseVO;
import com.kh.khedu.vo.payroll.response.ContractSignResponseVO;
import com.kh.khedu.vo.payroll.response.ContractUpdateDraftResponseVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@Tag(
	name = "근로 계약 관련 컨트롤러",
	description = "직원의 근로계약 등록, 조회, 수정 및 계약 상태를 관리하는 API"
)

@CommonsApiResponse
@RestController
@RequestMapping("/api/contract")
public class ContractController {

	@Autowired
	private ContractService contractService;

	// 계약 대상 데스크 직원 인적사항 조회
	@GetMapping("/desk/{employeeNo}")
	public ContractEmployeeDeskResponseVO findDeskPersonInfo(
	        @PathVariable int employeeNo,
	        @CurrentUser TokenParseResponseVO parseVO) {
		System.out.println("findDeskPersonInfo 진입");
		System.out.println(parseVO);
		System.out.println("employeeNo = " + employeeNo);
	    return contractService.findDeskPersonInfo(employeeNo, parseVO);
	}


	// 계약 대상 강사 직원 인적사항 조회
	@GetMapping("/teacher/{employeeNo}")
	public ContractEmployeeTeacherResponseVO findTeacherPersonInfo(
	        @PathVariable int employeeNo,
	        @CurrentUser TokenParseResponseVO parseVO) {

	    return contractService.findTeacherPersonInfo(employeeNo, parseVO);
	}
	
	//계약 조회
	
	@GetMapping("/detail/{contractNo}")
	public ContractDetailResponseVO find (@PathVariable long contractNo, @CurrentUser TokenParseResponseVO parseVO) {
		return contractService.find(contractNo, parseVO);
	}
	

	// 신규 근로계약 작성 //권한 설정 완
	@PostMapping("/add")
	public ContractAddResponseVO add(
			@Valid @RequestBody ContractAddRequestVO request
			,@CurrentUser TokenParseResponseVO parseVO) {
	
		return contractService.add(request,parseVO);
	}


	// 양측 서명 완료 전 계약 수정 //권한 설정 완
	@PatchMapping("/editBefore/{contractNo}")
	public ContractUpdateDraftResponseVO updateDraft(
			@PathVariable long contractNo,
			@Valid @RequestBody ContractUpdateDraftRequestVO request,
			@CurrentUser TokenParseResponseVO parseVO) {

	return contractService.updateDraft(contractNo, request, parseVO);
	}

	//서명 전 작성 된 정보 불러오기
	@PatchMapping("recallBefore/{contractNo}")
	public ContractSignDetailResponseVO recallBefore(
			@PathVariable long contractNo,
			@CurrentUser TokenParseResponseVO parseVO) {
		 return contractService.recallBefore(
		            contractNo,
		            parseVO
		    );
	}

	// 을(직원) 서명 //권한 설정 완
	@PatchMapping("/{contractNo}/employeeSign")
	public void employeeSign(
			@PathVariable long contractNo,
			@Valid @RequestBody ContractEmployeeSignRequestVO request,
			@CurrentUser TokenParseResponseVO parseVO) {

		contractService.employeeSign(
				contractNo,
				request,
				parseVO
		);
	}


	// 갑(원장) 서명 //권한 설정 완
	@PatchMapping("/{contractNo}/employerSign")
	public void employerSign(
			@PathVariable long contractNo,
			@Valid @RequestBody ContractEmployerSignRequestVO request,
			@CurrentUser TokenParseResponseVO parseVO) {

		contractService.employerSign(
				contractNo,
				request,
				parseVO
		);
	}


	// 직원의 현재 근로계약 조회
	@GetMapping("/{employeeNo}/current")
	public ContractDetailResponseVO findCurrent(
			@PathVariable int employeeNo
			,@CurrentUser TokenParseResponseVO parseVO) {
		
		
		return contractService.findCurrent(
				employeeNo,parseVO
		);
	}


	// 직원의 과거(종료) 근로계약 조회
	@GetMapping("/{employeeNo}/past")
	public List<ContractHistoryResponseVO> findPast(
			@PathVariable int employeeNo
			,@CurrentUser TokenParseResponseVO parseVO) {

		return contractService.findPast(
				employeeNo,parseVO
		);
	}


	// 직원의 전체 근로계약 조회
	@GetMapping("/{employeeNo}")
	public List<ContractHistoryResponseVO> findAllByEmployee(
			@PathVariable int employeeNo
			,@CurrentUser TokenParseResponseVO parseVO) {

		return contractService.findAllByEmployee(
				employeeNo, parseVO
		);
	}


	// 체결 후 근로조건 변경
	@PostMapping("/{contractNo}/changeWorkCondition")
	public ContractChangeConditionResponseVO changeWorkCondition(
			@PathVariable long contractNo,
			@Valid @RequestBody ContractChangeConditionRequestVO request
			,@CurrentUser TokenParseResponseVO parseVO) {

		return contractService.changeWorkCondition(
				contractNo,
				request
				,parseVO
		);
	}

	
	//근로계약 종료(도중 퇴사)
	@PatchMapping("/{contractNo}/exit")
	public void exitContract(
			@PathVariable long contractNo
			,@CurrentUser TokenParseResponseVO parseVO) {
		contractService.exitContract(contractNo,parseVO);
	}


	// 계약 서명정보 조회
	@GetMapping("/{contractNo}/findSignature")
	public ContractSignResponseVO findSignature(
			@PathVariable long contractNo,
			@CurrentUser TokenParseResponseVO parseVO) {

		return contractService.findSignature(
				contractNo,
				parseVO
		);
	}
	
	//단순 기간 연장
	@PatchMapping("/{contractNo}/extend")
	public ContractExtendResponseVO extendContract(
	        @PathVariable long contractNo,
	        @Valid @RequestBody ContractExtendRequestVO request,
	        @CurrentUser TokenParseResponseVO parseVO) {

	    request.setContractNo(contractNo);

	    return contractService.extendContract(
	            request,
	            parseVO
	    );
	}
	
	
}