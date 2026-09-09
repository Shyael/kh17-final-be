package com.kh.khedu.service.payroll;

import java.util.List;

import com.kh.khedu.vo.payroll.response.PayrollDetailResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollMonthlyListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollPaymentResponseVO;

public interface PayrollService {

	// 직원 + 연월 급여 최초 계산
	void calculate(
			int employeeNo,
			int payrollYear,
			int payrollMonth
	);

	// 직원 + 연월 급여 재계산
	void recalculate(
			int employeeNo,
			int payrollYear,
			int payrollMonth
);

	// 급여 확정
	void confirm(
			int employeeNo,
			int payrollYear,
			int payrollMonth
	);
	
	
	void pay(
			int employeeNo,
			int payrollYear,
			int payrollMonth,
			String paymentMethod,
			String paymentNote
	);
	
	
	void cancelPayment(
			int employeeNo,
			int payrollYear,
			int payrollMonth,
			long cancelAmount,
			String paymentNote
	);
	
	
	PayrollDetailResponseVO findDetail(
			int employeeNo,
			int payrollYear,
			int payrollMonth
	);

	List<PayrollPaymentResponseVO> findPaymentHistory(
			int employeeNo,
			int payrollYear,
			int payrollMonth
	);
	
	// 직원 급여 목록 조회
	List<PayrollListResponseVO> findAllByEmployee(
			int employeeNo
	);

	
	List<PayrollMonthlyListResponseVO> findAllByPeriod(
			int payrollYear,
			int payrollMonth
	);

}