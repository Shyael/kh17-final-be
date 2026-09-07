package com.kh.khedu.dao.payroll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.payroll.PayrollDto;

@Repository
public class PayrollDaoMybatis implements PayrollDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public long sequence() {
		return sqlSession.selectOne(
				"mapper.payroll.payrollSequence"
		);
	}

	@Override
	public PayrollDto findByEmployeeAndPeriod(
			long employeeNo,
			int payrollYear,
			int payrollMonth) {

		Map<String, Object> param = new HashMap<>();

		param.put("employeeNo", employeeNo);
		param.put("payrollYear", payrollYear);
		param.put("payrollMonth", payrollMonth);

		return sqlSession.selectOne(
				"mapper.payroll.payrollFindByEmployeeAndPeriod",
				param
		);
	}

	@Override
	public void add(PayrollDto payrollDto) {
		sqlSession.insert(
				"mapper.payroll.payrollAdd",
				payrollDto
		);
	}

	@Override
	public PayrollDto find(long payrollNo) {
		return sqlSession.selectOne(
				"mapper.payroll.payrollFind",
				payrollNo
		);
	}

	@Override
	public boolean updateCalculation(PayrollDto payrollDto) {
		return sqlSession.update(
				"mapper.payroll.payrollUpdateCalculation",
				payrollDto
		) > 0;
	}

	@Override
	public boolean changeStatus(PayrollDto payrollDto) {
		return sqlSession.update(
				"mapper.payroll.payrollChangeStatus",
				payrollDto
		) > 0;
	}

	@Override
	public List<PayrollDto> findAllByEmployee(long employeeNo) {
		return sqlSession.selectList(
				"mapper.payroll.payrollFindAllByEmployee",
				employeeNo
		);
	}

}