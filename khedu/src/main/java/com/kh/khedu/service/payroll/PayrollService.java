package com.kh.khedu.service.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.PayrollDto;
import com.kh.khedu.dto.payroll.PayrollPaymentDto;

public interface PayrollService {

	// 직원 + 연월 급여 최초 계산
	void calculate(
			int employeeNo,
			int payrollYear,
			int payrollMonth
	);

	// 직원 + 연월 급여 재계산
	void recalculate(
			long employeeNo,
			int payrollYear,
			int payrollMonth
);
//
//	// 직원 + 연월 급여 상세 조회
//	PayrollDto findByEmployeeAndPeriod(
//			long employeeNo,
//			int payrollYear,
//			int payrollMonth
//	);
//
//	// 직원 급여 목록 조회
//	List<PayrollDto> findAllByEmployee(
//			long employeeNo
//	);
//
//	// 급여 확정
//	void confirm(
//			long employeeNo,
//			int payrollYear,
//			int payrollMonth
//	);
//
//	// 급여 지급
//	void pay(
//			long employeeNo,
//			int payrollYear,
//			int payrollMonth
//	);
//
//	// 지급 취소
//	void cancelPayment(
//			long payrollPaymentNo
//	);
//
//	// 지급 이력 조회
//	List<PayrollPaymentDto> findPaymentHistory(
//			long employeeNo,
//			int payrollYear,
//			int payrollMonth
//	);

}