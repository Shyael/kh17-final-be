package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.PayrollDeductionDto;

public interface PayrollDeductionDao {

	// 공제번호 생성
	long sequence();

	// 공제항목 등록
	void add(PayrollDeductionDto payrollDeductionDto);
	
	//급여 수정시 공제도 수정되도록, 단순 업데이트를 채택한 이유는 거래 이력은 급여테이블에서 관리 되고 공제는 그에 따라 수정되기만 하면 된다고 생각하기 때문입니다.
	boolean update(PayrollDeductionDto payrollDeductiondto);

	// 급여번호를 이용해 공제목록 조회
	List<PayrollDeductionDto> findAllByPayroll(long payrollNo);

}