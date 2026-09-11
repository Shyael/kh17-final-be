package com.kh.khedu.dao.payroll;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.payroll.PayrollDeductionDto;

@Repository
public class PayrollDeductionDaoMybatis implements PayrollDeductionDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public long sequence() {
		return sqlSession.selectOne(
				"mapper.payroll.deductionSequence"
		);
	}

	@Override
	public void add(PayrollDeductionDto payrollDeductionDto) {
		sqlSession.insert(
				"mapper.payroll.deductionAdd",
				payrollDeductionDto
		);
	}

	@Override
	public List<PayrollDeductionDto> findAllByPayroll(long payrollNo) {
		return sqlSession.selectList(
				"mapper.payroll.deductionFindAllByPayroll",
				payrollNo
		);
	}

	@Override
	public boolean update(PayrollDeductionDto payrollDeductiondto) {
		return sqlSession.update("mapper.payroll.deductionUpdate",payrollDeductiondto)>0;
	}

}