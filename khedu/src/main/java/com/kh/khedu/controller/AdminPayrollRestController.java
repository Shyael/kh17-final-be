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

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.error.AdminChecker;
import com.kh.khedu.service.payroll.PayrollService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.PayrollPayRequestVO;
import com.kh.khedu.vo.payroll.request.PayrollPaymentCancelRequestVO;
import com.kh.khedu.vo.payroll.request.PayrollPeriodRequestVO;
import com.kh.khedu.vo.payroll.response.PayrollDetailResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollMonthlyListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollPaymentResponseVO;

@RestController
@RequestMapping("/api/admin/payroll")
public class AdminPayrollRestController {

	@Autowired
	private PayrollService payrollService;

	@Autowired
	private AdminChecker adminChecker;
	// =========================
	// 급여 최초 계산
	// =========================

	@PostMapping("/calculate")
	public void calculate(
			@RequestBody PayrollPeriodRequestVO requestVO
			,@CurrentUser TokenParseResponseVO parseVO) {

		adminChecker.AdminCheck(parseVO);
		payrollService.calculate(
				requestVO.getEmployeeNo(),
				requestVO.getPayrollYear(),
				requestVO.getPayrollMonth()
		);
	}


	// =========================
	// 급여 재계산
	// =========================

	@PatchMapping("/recalculate")
	public void recalculate(
			@RequestBody PayrollPeriodRequestVO requestVO
			,@CurrentUser TokenParseResponseVO parseVO) {
		adminChecker.AdminCheck(parseVO);
		payrollService.recalculate(
				requestVO.getEmployeeNo(),
				requestVO.getPayrollYear(),
				requestVO.getPayrollMonth()
		);
	}


	// =========================
	// 급여 확정
	// =========================

	@PatchMapping("/confirm")
	public void confirm(
			@RequestBody PayrollPeriodRequestVO requestVO
			,@CurrentUser TokenParseResponseVO parseVO) {
		adminChecker.AdminCheck(parseVO);
		payrollService.confirm(
				requestVO.getEmployeeNo(),
				requestVO.getPayrollYear(),
				requestVO.getPayrollMonth()
		);
	}


	// =========================
	// 급여 지급
	// =========================

	@PostMapping("/pay")
	public void pay(
			@RequestBody PayrollPayRequestVO requestVO
			,@CurrentUser TokenParseResponseVO parseVO) {
		adminChecker.AdminCheck(parseVO);
		payrollService.pay(
				requestVO.getEmployeeNo(),
				requestVO.getPayrollYear(),
				requestVO.getPayrollMonth(),
				requestVO.getPaymentMethod(),
				requestVO.getPaymentNote()
		);
	}


	// =========================
	// 급여 지급 취소
	// =========================

	@PostMapping("/cancel-payment")
	public void cancelPayment(
			@RequestBody PayrollPaymentCancelRequestVO requestVO
			,@CurrentUser TokenParseResponseVO parseVO) {
		adminChecker.AdminCheck(parseVO);
		payrollService.cancelPayment(
				requestVO.getEmployeeNo(),
				requestVO.getPayrollYear(),
				requestVO.getPayrollMonth(),
				requestVO.getCancelAmount(),
				requestVO.getPaymentNote()
		);
	}


	// =========================
	// 월 급여 상세 조회
	// =========================

	@GetMapping("/detail/{employeeNo}/{payrollYear}/{payrollMonth}")
	public PayrollDetailResponseVO findDetail(
			@PathVariable int employeeNo,
			@PathVariable int payrollYear,
			@PathVariable int payrollMonth
			,@CurrentUser TokenParseResponseVO parseVO) {
		adminChecker.AdminCheck(parseVO);
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
		adminChecker.AdminCheck(parseVO);
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
		adminChecker.AdminCheck(parseVO);
		return payrollService.findPaymentHistory(
				employeeNo,
				payrollYear,
				payrollMonth
		);
	}
	
	@GetMapping("/monthly/{payrollYear}/{payrollMonth}")
	public List<PayrollMonthlyListResponseVO> findAllByPeriod(
			@PathVariable int payrollYear,
			@PathVariable int payrollMonth
			,@CurrentUser TokenParseResponseVO parseVO) {
		adminChecker.AdminCheck(parseVO);
		return payrollService.findAllByPeriod(
				payrollYear,
				payrollMonth
		);
	}
	
}