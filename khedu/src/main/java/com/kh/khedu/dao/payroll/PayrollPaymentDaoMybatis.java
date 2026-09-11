package com.kh.khedu.dao.payroll;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.payroll.PayrollPaymentDto;

@Repository
public class PayrollPaymentDaoMybatis implements PayrollPaymentDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public long sequence() {
		return sqlSession.selectOne(
				"mapper.payroll.paymentSequence"
		);
	}

	@Override
	public void add(PayrollPaymentDto payrollPaymentDto) {
		sqlSession.insert(
				"mapper.payroll.paymentAdd",
				payrollPaymentDto
		);
	}

	@Override
	public PayrollPaymentDto find(long payrollPaymentNo) {
		return sqlSession.selectOne(
				"mapper.payroll.paymentFind",
				payrollPaymentNo
		);
	}

	@Override
	public List<PayrollPaymentDto> findAllByPayroll(long payrollNo) {
		return sqlSession.selectList(
				"mapper.payroll.paymentFindAllByPayroll",
				payrollNo
		);
	}

	@Override
	public List<PayrollPaymentDto> findCancelledByPayroll(long payrollNo) {
		return sqlSession.selectList(
				"mapper.payroll.paymentFindCancelledByPayroll",
				payrollNo
		);
	}

	@Override
	public List<PayrollPaymentDto> findNotCancelledByPayroll(long payrollNo) {
		return sqlSession.selectList(
				"mapper.payroll.paymentFindNotCancelledByPayroll",
				payrollNo
		);
	}

}