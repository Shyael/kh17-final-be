package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.PayrollDeductionDto;

public interface PayrollDeductionDao {

	// 공제번호 생성
	long sequence();

	// 공제항목 등록
	void add(PayrollDeductionDto payrollDeductionDto);

	// 급여번호를 이용해 공제목록 조회
	List<PayrollDeductionDto> findAllByPayroll(long payrollNo);

}