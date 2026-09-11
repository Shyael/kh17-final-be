package com.kh.khedu.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.error.AdminChecker;
import com.kh.khedu.service.payroll.ContractService;
import com.kh.khedu.util.PageResponseVO;
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

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@Tag(
	name = "근로 계약 관련 컨트롤러",
	description = "직원의 근로계약 등록, 조회, 수정 및 계약 상태를 관리하는 API"
)

@CommonsApiResponse
@RestController
@RequestMapping("/api/employee/contract")
public class ContractController {

	@Autowired
	private ContractService contractService;
	

	

	
	//계약 조회
	
	@GetMapping("/detail/{contractNo}")
	public ContractDetailResponseVO find (@PathVariable long contractNo, @CurrentUser TokenParseResponseVO parseVO) {
		return contractService.find(contractNo, parseVO);
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
	
	
	
	
	
}