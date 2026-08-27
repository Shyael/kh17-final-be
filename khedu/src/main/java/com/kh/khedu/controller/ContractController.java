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
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.requestvo.payroll.ContractAddRequestVO;
import com.kh.khedu.requestvo.payroll.ContractSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractUpdateRequestVO;
import com.kh.khedu.responsevo.payroll.ContractSignResponseVO;
import com.kh.khedu.service.payroll.ContractService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.tags.Tag;

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


	// 신규 근로계약 작성
	@PostMapping("/add")
	public ContractDto add(
			@RequestBody ContractAddRequestVO request) {

		return contractService.add(request);
	}


	// 양측 서명 완료 전 계약 수정
	@PatchMapping("/{contractNo}")
	public void update(
			@PathVariable long contractNo,
			@RequestBody ContractUpdateRequestVO request,
			TokenParseResponseVO parseVO) {

		contractService.update(
				contractNo,
				request,
				parseVO
		);
	}


	// 을(직원) 서명
	@PatchMapping("/{contractNo}/employeeSign")
	public void employeeSign(
			@PathVariable long contractNo,
			@RequestBody ContractSignRequestVO request,
			TokenParseResponseVO parseVO) {

		contractService.employeeSign(
				contractNo,
				request,
				parseVO
		);
	}


	// 갑(원장) 서명
	@PatchMapping("/{contractNo}/employerSign")
	public void employerSign(
			@PathVariable long contractNo,
			@RequestBody ContractSignRequestVO request,
			TokenParseResponseVO parseVO) {

		contractService.employerSign(
				contractNo,
				request,
				parseVO
		);
	}


	// 직원의 전체 근로계약 조회
	@GetMapping("/employee/{employeeNo}")
	public List<ContractDto> findAllByEmployee(
			@PathVariable long employeeNo) {

		return contractService.findAllByEmployee(
				employeeNo
		);
	}


	// 직원의 현재 근로계약 조회
	@GetMapping("/employee/{employeeNo}/current")
	public ContractDto findCurrent(
			@PathVariable long employeeNo) {

		return contractService.findCurrent(
				employeeNo
		);
	}


	// 체결 후 근로조건 변경
	@PostMapping("/{contractNo}/changeWorkCondition")
	public ContractDto changeWorkCondition(
			@PathVariable long contractNo,
			@RequestBody ContractUpdateRequestVO request) {

		return contractService.changeWorkCondition(
				contractNo,
				request
		);
	}


	// 근로계약 종료
	@PatchMapping("/{contractNo}/end")
	public void endContract(
			@PathVariable long contractNo) {

		contractService.endContract(
				contractNo
		);
	}


	// 계약 서명정보 조회
	@GetMapping("/{contractNo}/findSignature")
	public ContractSignResponseVO findSignature(
			@PathVariable long contractNo,
			TokenParseResponseVO parseVO) {

		return contractService.findSignature(
				contractNo,
				parseVO
		);
	}

}