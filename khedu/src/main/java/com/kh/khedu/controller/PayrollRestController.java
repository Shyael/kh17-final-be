package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.error.PrincipalChecker;
import com.kh.khedu.service.payroll.PayrollService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollDetailResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollPaymentResponseVO;

@RestController
@RequestMapping("/api/employee/payroll")
public class PayrollRestController {

	@Autowired
	private PayrollService payrollService;

	@Autowired
	private PrincipalChecker principalChecker;


	// =========================
	// 월 급여 상세 조회
	// =========================

	@GetMapping("/detail/{employeeNo}/{payrollYear}/{payrollMonth}")
	public PayrollDetailResponseVO findDetail(
			@PathVariable int employeeNo,
			@PathVariable int payrollYear,
			@PathVariable int payrollMonth
			,@CurrentUser TokenParseResponseVO parseVO) {
		
		principalChecker.CheckYou(parseVO, employeeNo);
		
		return payrollService.findDetail(
				employeeNo,
				payrollYear,
				payrollMonth
		);
	}


	// =========================
	// 직원 급여 목록 조회
	// =========================

	@GetMapping("/list/{employeeNo}")
	public List<PayrollListResponseVO> findAllByEmployee(
			@PathVariable int employeeNo
			,@CurrentUser TokenParseResponseVO parseVO) {

		principalChecker.CheckYou(parseVO, employeeNo);
		return payrollService.findAllByEmployee(
				employeeNo
		);
	}


	// =========================
	// 지급 / 취소 이력 조회
	// =========================

	@GetMapping("/payment-history/{employeeNo}/{payrollYear}/{payrollMonth}")
	public List<PayrollPaymentResponseVO> findPaymentHistory(
			@PathVariable int employeeNo,
			@PathVariable int payrollYear,
			@PathVariable int payrollMonth
			,@CurrentUser TokenParseResponseVO parseVO) {
		principalChecker.CheckYou(parseVO, employeeNo);
		return payrollService.findPaymentHistory(
				employeeNo,
				payrollYear,
				payrollMonth
		);
	}
}