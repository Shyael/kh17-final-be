package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.PayrollPaymentDto;

public interface PayrollPaymentDao {

	// 지급내역번호 생성
	long sequence();

	// 지급 / 취소 이력 등록
	void add(PayrollPaymentDto payrollPaymentDto);

	// 지급내역번호를 이용해 지급내역 조회
	PayrollPaymentDto find(long payrollPaymentNo);

	// 급여번호를 이용해 전체 지급이력 조회
	List<PayrollPaymentDto> findAllByPayroll(long payrollNo);

	// 급여번호를 이용해 취소된 지급내역 조회
	List<PayrollPaymentDto> findCancelledByPayroll(long payrollNo);

	// 급여번호를 이용해 취소되지 않은 지급내역 조회
	List<PayrollPaymentDto> findNotCancelledByPayroll(long payrollNo);

}