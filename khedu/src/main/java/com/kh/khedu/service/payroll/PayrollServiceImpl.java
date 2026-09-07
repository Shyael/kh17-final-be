package com.kh.khedu.service.payroll;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.dao.payroll.EmployeeWorkScheduleDao;
import com.kh.khedu.dao.payroll.PayrollDao;
import com.kh.khedu.dao.payroll.PayrollDeductionDao;
import com.kh.khedu.dao.payroll.PayrollPaymentDao;
import com.kh.khedu.dto.payroll.PayrollDeductionDto;
import com.kh.khedu.dto.payroll.PayrollDto;
import com.kh.khedu.dto.payroll.PayrollPaymentDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

	@Autowired
	private PayrollDao payrollDao;

	@Autowired
	private ContractDao contractDao;

	@Autowired
	private EmployeeWorkScheduleDao employeeWorkScheduleDao;

	@Autowired
	private EmployeeAttendanceDao employeeAttendanceDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private PayrollDeductionDao payrollDeductionDao;
	
	@Autowired
	private PayrollPaymentDao payrollPaymentDao;
	
	@Autowired
	private Calculater calculater;

	@Override
	@Transactional
	public void calculate(int employeeNo, int payrollYear, int payrollMonth) {

		// 이미 해당 월 급여가 존재하는지 확인
		PayrollDto findDto = payrollDao.findByEmployeeAndPeriod(employeeNo, payrollYear, payrollMonth);

		if (findDto != null) {
			throw new GetOutException();
		}
		long payrollNo =
				payrollDao.sequence();
		
		
		PayrollDto payrollDto = calculater.calculatePayroll(payrollNo,payrollMonth, employeeNo, payrollYear);
		
		
		payrollDto.setPayrollNo(payrollNo);
		

		// =========================
		// 급여 기본 계산 결과 저장
		// =========================

		payrollDao.add(
				payrollDto
		);
		
		// =========================
		// 공제 계산
		// =========================

		long totalDeduction = 0;


		// 현재 KH EDU 단순화 기준
		// 실제 국민연금 기준소득월액,
		// 건강보험 / 고용보험 보수월액과는 차이가 있을 수 있음
		long insuranceBaseAmount =
				payrollDto.getGrossPay();


		// =========================
		// 국민연금
		// =========================

		double pensionRate =
				0.0475;

		long pension =
				Math.round(
						insuranceBaseAmount
						* pensionRate
				);


		PayrollDeductionDto pensionDto =
				PayrollDeductionDto.builder()
						.deductionNo(
								payrollDeductionDao.sequence()
						)
						.payrollNo(payrollNo)
						.deductionType("국민연금")
						.baseAmount(insuranceBaseAmount)
						.deductionRate(pensionRate)
						.fixedDeductionAmount(null)
						.deductionAmount(pension)
						.deductionBasisYear(payrollYear)
						.deductionNote("근로자 부담 국민연금")
						.build();


		payrollDeductionDao.add(
				pensionDto
		);

		totalDeduction += pension;


		// =========================
		// 건강보험
		// =========================

		// 전체 7.19%
		// 근로자 50% 부담
		double healthInsuranceRate =
				0.03595;

		long healthInsurance =
				Math.round(
						insuranceBaseAmount
						* healthInsuranceRate
				);


		PayrollDeductionDto healthInsuranceDto =
				PayrollDeductionDto.builder()
						.deductionNo(
								payrollDeductionDao.sequence()
						)
						.payrollNo(payrollNo)
						.deductionType("건강보험")
						.baseAmount(insuranceBaseAmount)
						.deductionRate(healthInsuranceRate)
						.fixedDeductionAmount(null)
						.deductionAmount(healthInsurance)
						.deductionBasisYear(payrollYear)
						.deductionNote("근로자 부담 건강보험")
						.build();


		payrollDeductionDao.add(
				healthInsuranceDto
		);

		totalDeduction += healthInsurance;


		// =========================
		// 장기요양보험
		// =========================

		// 2026
		// 장기요양보험료율 0.9448%
		// 건강보험료율 7.19%
		//
		// 장기요양보험료
		// = 건강보험료 × 0.9448 / 7.19

		double longTermCareRate =
				0.009448
				/ 0.0719;


		long longTermCareInsurance =
				Math.round(
						healthInsurance
						* longTermCareRate
				);


		PayrollDeductionDto longTermCareDto =
				PayrollDeductionDto.builder()
						.deductionNo(
								payrollDeductionDao.sequence()
						)
						.payrollNo(payrollNo)
						.deductionType("장기요양보험")

						// 장기요양은
						// 건강보험 근로자 부담액을 기준으로 계산
						.baseAmount(healthInsurance)

						.deductionRate(longTermCareRate)
						.fixedDeductionAmount(null)
						.deductionAmount(longTermCareInsurance)
						.deductionBasisYear(payrollYear)
						.deductionNote("건강보험료 기준 장기요양보험")
						.build();


		payrollDeductionDao.add(
				longTermCareDto
		);

		totalDeduction +=
				longTermCareInsurance;


		// =========================
		// 고용보험
		// =========================

		double employmentInsuranceRate =
				0.009;

		long employmentInsurance =
				Math.round(
						insuranceBaseAmount
						* employmentInsuranceRate
				);


		PayrollDeductionDto employmentInsuranceDto =
				PayrollDeductionDto.builder()
						.deductionNo(
								payrollDeductionDao.sequence()
						)
						.payrollNo(payrollNo)
						.deductionType("고용보험")
						.baseAmount(insuranceBaseAmount)
						.deductionRate(employmentInsuranceRate)
						.fixedDeductionAmount(null)
						.deductionAmount(employmentInsurance)
						.deductionBasisYear(payrollYear)
						.deductionNote("근로자 부담 고용보험")
						.build();


		payrollDeductionDao.add(
				employmentInsuranceDto
		);

		
		// =========================
		// 총 공제액
		// =========================

		
		totalDeduction +=
				employmentInsurance;
		
		

		// =========================
		// 실수령액
		// =========================

		long netPay =
				payrollDto.getGrossPay()
				- totalDeduction;


		// =========================
		// 급여 계산 완료 처리
		// =========================

		payrollDto.setTotalDeduction(
				totalDeduction
		);

		payrollDto.setNetPay(
				netPay
		);

		payrollDto.setCalculatedAt(
				new Timestamp(
						System.currentTimeMillis()
				)
		);


		// 계산 결과 최종 반영
		boolean result =
				payrollDao.updateCalculation(
						payrollDto
				);

		if (!result) {
			throw new TargetNotfoundException();
		}
	}

	
	@Override
	public void recalculate(
			long employeeNo,
			int payrollYear,
			int payrollMonth) {

		// 기존 급여 조회
		PayrollDto payrollDto =
				payrollDao.findByEmployeeAndPeriod(
						employeeNo,
						payrollYear,
						payrollMonth
				);

		if (payrollDto == null) {
			throw new TargetNotfoundException();
		}


		long payrollNo =
				payrollDto.getPayrollNo();


		// =========================
		// 취소되지 않은 지급 확인
		// =========================

		List<PayrollPaymentDto> paymentList =
				payrollPaymentDao
						.findNotCancelledByPayroll(
								payrollNo
						);

		// 실제 지급됐고 아직 취소되지 않았다면
		// 재계산 불가
		if (!paymentList.isEmpty()) {
			throw new GetOutException();
		}


		// =========================
		// 다시 계산중 상태
		// =========================

		payrollDto.setPayrollStatus(
				"calculating"
		);

		payrollDto.setConfirmedAt(
				null
		);

		payrollDao.changeStatus(
				payrollDto
		);


		// =========================
		// 급여 재계산
		// =========================

		// 여기서 Contract / Schedule을 다시 읽고
		// basePay
		// weekHolidayPay
		// overtimePay
		// nightPay
		// holidayPay
		// grossPay
		// 를 현재 calculate()와 동일한 방식으로 다시 계산
		
		PayrollDto rePayrollDto = calculater.calculatePayroll(payrollNo, payrollMonth, payrollYear, payrollMonth);
		
	}
	
}
/*
 * 이후 구현 순서
 *
 * 1. 계약별 basePay 계산
 *
 * 2. 연장수당 계산
 *
 * 3. 야간수당 계산
 *
 * 4. 휴일근로수당 계산
 *
 * 5. 주휴수당 계산
 *
 * 6. 계약별 결과 월 전체 합산
 *
 * 7. grossPay 계산
 *
 * 8. PayrollDto 생성
 *
 * 9. payrollDao.add()
 *
 * 10. 공제 계산
 *
 * 11. totalDeduction 계산
 *
 * 12. netPay 계산
 * 
 */
