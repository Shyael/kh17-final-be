package com.kh.khedu.service.payroll;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.PayrollDao;
import com.kh.khedu.dao.payroll.PayrollDeductionDao;
import com.kh.khedu.dao.payroll.PayrollPaymentDao;
import com.kh.khedu.dto.payroll.PayrollDeductionDto;
import com.kh.khedu.dto.payroll.PayrollDto;
import com.kh.khedu.dto.payroll.PayrollPaymentDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.admin.employee.AdminEmployeeDetailVO;
import com.kh.khedu.vo.payroll.response.PayrollDeductionResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollDetailResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollMonthlyListResponseVO;
import com.kh.khedu.vo.payroll.response.PayrollPaymentResponseVO;
@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

	@Autowired
	private PayrollDao payrollDao;

	@Autowired
	private PayrollDeductionDao payrollDeductionDao;
	
	@Autowired
	private PayrollPaymentDao payrollPaymentDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private ContractDao contractDao;
	
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
		
		
		PayrollDto payrollDto = calculater.calculatePayroll(payrollNo,employeeNo,payrollYear,payrollMonth);
		
		
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
			int employeeNo,
			int payrollYear,
			int payrollMonth) {

		// =========================
		// 기존 급여 조회
		// =========================

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

		// 이미 지급됐고 아직 취소되지 않은 지급내역이 존재하면
		// 급여 재계산 불가
		if (!paymentList.isEmpty()) {
			throw new GetOutException();
		}


		// =========================
		// 급여 다시 계산
		// =========================

		PayrollDto rePayrollDto =
				calculater.calculatePayroll(
						payrollNo,
						employeeNo,
						payrollYear,
						payrollMonth
				);


		// =========================
		// 기존 공제내역 조회
		// =========================

		List<PayrollDeductionDto> deductionList =
				payrollDeductionDao
						.findAllByPayroll(
								payrollNo
						);


		// =========================
		// 공제 계산
		// =========================

		long totalDeduction = 0;


		// 현재 KH EDU 단순화 기준
		long insuranceBaseAmount =
				rePayrollDto.getGrossPay();


		// =================================================
		// 국민연금
		// =================================================

		double pensionRate =
				0.0475;


		long pension =
				Math.round(
						insuranceBaseAmount
						* pensionRate
				);


		PayrollDeductionDto pensionDto =
				PayrollDeductionDto.builder()
						.payrollNo(payrollNo)
						.deductionType("국민연금")
						.baseAmount(insuranceBaseAmount)
						.deductionRate(pensionRate)
						.fixedDeductionAmount(null)
						.deductionAmount(pension)
						.deductionBasisYear(payrollYear)
						.deductionNote("근로자 부담 국민연금")
						.build();


		PayrollDeductionDto oldPensionDto =
				null;


		for (PayrollDeductionDto deductionDto
				: deductionList) {

			if ("국민연금".equals(
					deductionDto.getDeductionType())) {

				oldPensionDto =
						deductionDto;

				break;
			}
		}


		if (oldPensionDto != null) {

			pensionDto.setDeductionNo(
					oldPensionDto.getDeductionNo()
			);

			payrollDeductionDao.update(
					pensionDto
			);
		}

		else {

			pensionDto.setDeductionNo(
					payrollDeductionDao.sequence()
			);

			payrollDeductionDao.add(
					pensionDto
			);
		}


		totalDeduction +=
				pension;


		// =================================================
		// 건강보험
		// =================================================

		double healthInsuranceRate =
				0.03595;


		long healthInsurance =
				Math.round(
						insuranceBaseAmount
						* healthInsuranceRate
				);


		PayrollDeductionDto healthInsuranceDto =
				PayrollDeductionDto.builder()
						.payrollNo(payrollNo)
						.deductionType("건강보험")
						.baseAmount(insuranceBaseAmount)
						.deductionRate(healthInsuranceRate)
						.fixedDeductionAmount(null)
						.deductionAmount(healthInsurance)
						.deductionBasisYear(payrollYear)
						.deductionNote("근로자 부담 건강보험")
						.build();


		PayrollDeductionDto oldHealthInsuranceDto =
				null;


		for (PayrollDeductionDto deductionDto
				: deductionList) {

			if ("건강보험".equals(
					deductionDto.getDeductionType())) {

				oldHealthInsuranceDto =
						deductionDto;

				break;
			}
		}


		if (oldHealthInsuranceDto != null) {

			healthInsuranceDto.setDeductionNo(
					oldHealthInsuranceDto.getDeductionNo()
			);

			payrollDeductionDao.update(
					healthInsuranceDto
			);
		}

		else {

			healthInsuranceDto.setDeductionNo(
					payrollDeductionDao.sequence()
			);

			payrollDeductionDao.add(
					healthInsuranceDto
			);
		}


		totalDeduction +=
				healthInsurance;


		// =================================================
		// 장기요양보험
		// =================================================

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
						.payrollNo(payrollNo)
						.deductionType("장기요양보험")
						.baseAmount(healthInsurance)
						.deductionRate(longTermCareRate)
						.fixedDeductionAmount(null)
						.deductionAmount(longTermCareInsurance)
						.deductionBasisYear(payrollYear)
						.deductionNote("건강보험료 기준 장기요양보험")
						.build();


		PayrollDeductionDto oldLongTermCareDto =
				null;


		for (PayrollDeductionDto deductionDto
				: deductionList) {

			if ("장기요양보험".equals(
					deductionDto.getDeductionType())) {

				oldLongTermCareDto =
						deductionDto;

				break;
			}
		}


		if (oldLongTermCareDto != null) {

			longTermCareDto.setDeductionNo(
					oldLongTermCareDto.getDeductionNo()
			);

			payrollDeductionDao.update(
					longTermCareDto
			);
		}

		else {

			longTermCareDto.setDeductionNo(
					payrollDeductionDao.sequence()
			);

			payrollDeductionDao.add(
					longTermCareDto
			);
		}


		totalDeduction +=
				longTermCareInsurance;


		// =================================================
		// 고용보험
		// =================================================

		double employmentInsuranceRate =
				0.009;


		long employmentInsurance =
				Math.round(
						insuranceBaseAmount
						* employmentInsuranceRate
				);


		PayrollDeductionDto employmentInsuranceDto =
				PayrollDeductionDto.builder()
						.payrollNo(payrollNo)
						.deductionType("고용보험")
						.baseAmount(insuranceBaseAmount)
						.deductionRate(employmentInsuranceRate)
						.fixedDeductionAmount(null)
						.deductionAmount(employmentInsurance)
						.deductionBasisYear(payrollYear)
						.deductionNote("근로자 부담 고용보험")
						.build();


		PayrollDeductionDto oldEmploymentInsuranceDto =
				null;


		for (PayrollDeductionDto deductionDto
				: deductionList) {

			if ("고용보험".equals(
					deductionDto.getDeductionType())) {

				oldEmploymentInsuranceDto =
						deductionDto;

				break;
			}
		}


		if (oldEmploymentInsuranceDto != null) {

			employmentInsuranceDto.setDeductionNo(
					oldEmploymentInsuranceDto.getDeductionNo()
			);

			payrollDeductionDao.update(
					employmentInsuranceDto
			);
		}

		else {

			employmentInsuranceDto.setDeductionNo(
					payrollDeductionDao.sequence()
			);

			payrollDeductionDao.add(
					employmentInsuranceDto
			);
		}


		totalDeduction +=
				employmentInsurance;


		// =================================================
		// 실수령액 계산
		// =================================================

		long netPay =
				rePayrollDto.getGrossPay()
				- totalDeduction;


		// =================================================
		// 재계산 결과 반영
		// =================================================

		rePayrollDto.setTotalDeduction(
				totalDeduction
		);

		rePayrollDto.setNetPay(
				netPay
		);

		rePayrollDto.setPayrollStatus(
				"calculating"
		);

		rePayrollDto.setCalculatedAt(
				new Timestamp(
						System.currentTimeMillis()
				)
		);

		// confirmed였던 급여도 재계산하면
		// 다시 calculating으로 돌아가므로 확정시간 초기화
		rePayrollDto.setConfirmedAt(
				null
		);


		// =========================
		// 급여 재계산 결과 수정
		// =========================

		boolean result =
				payrollDao.updateCalculation(
						rePayrollDto
				);

		if (!result) {
			throw new TargetNotfoundException();
		}
	}
	
	
	@Override
	public void confirm(
			int employeeNo,
			int payrollYear,
			int payrollMonth) {

		PayrollDto payrollDto =
				payrollDao.findByEmployeeAndPeriod(
						employeeNo,
						payrollYear,
						payrollMonth
				);

		if (payrollDto == null) {
			throw new TargetNotfoundException();
		}

		if ("confirmed".equals(
				payrollDto.getPayrollStatus())) {

			throw new GetOutException();
		}

		if (payrollDto.getCalculatedAt() == null) {
			throw new GetOutException();
		}

		payrollDto.setPayrollStatus(
				"confirmed"
		);

		payrollDto.setConfirmedAt(
				new Timestamp(
						System.currentTimeMillis()
				)
		);

		boolean result =
				payrollDao.changeStatus(
						payrollDto
				);

		if (!result) {
			throw new TargetNotfoundException();
		}
	}
	
	
	
	@Override
	public void pay(
			int employeeNo,
			int payrollYear,
			int payrollMonth,
			String paymentMethod,
			String paymentNote) {

		// =========================
		// 급여 조회
		// =========================

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
		// 급여 확정 여부 확인
		// =========================

		// 확정되지 않은 급여는 지급 불가
		if (!"confirmed".equals(
				payrollDto.getPayrollStatus())) {

			throw new GetOutException();
		}


		// =========================
		// 급여 계산 완료 여부 확인
		// =========================

		if (payrollDto.getCalculatedAt() == null) {
			throw new GetOutException();
		}


		// =========================
		// 실수령액 확인
		// =========================

		if (payrollDto.getNetPay() == null
				|| payrollDto.getNetPay() <= 0) {

			throw new GetOutException();
		}


		// =========================
		// 기존 지급내역 확인
		// =========================

		List<PayrollPaymentDto> paymentList =
				payrollPaymentDao
						.findNotCancelledByPayroll(
								payrollNo
						);


		// 취소되지 않은 기존 지급내역이 존재하면
		// 중복 지급 불가
		if (!paymentList.isEmpty()) {
			throw new GetOutException();
		}


		// =========================
		// 지급수단 확인
		// =========================

		if (paymentMethod == null
				|| paymentMethod.isBlank()) {

			throw new GetOutException();
		}


		// =========================
		// 지급번호 생성
		// =========================

		long payrollPaymentNo =
				payrollPaymentDao.sequence();


		// =========================
		// 지급내역 생성
		// =========================

		PayrollPaymentDto paymentDto =
				PayrollPaymentDto.builder()
						.payrollPaymentNo(
								payrollPaymentNo
						)

						.payrollNo(
								payrollNo
						)

						// 지급액은 확정된 실수령액
						.paymentAmount(
								payrollDto.getNetPay()
						)

						.paymentAt(
								new Timestamp(
										System.currentTimeMillis()
								)
						)

						.paymentMethod(
								paymentMethod
						)

						.paymentNote(
								paymentNote
						)

						.paymentStatus(
								"paid"
						)

						// 정상 지급은 취소 대상 없음
						.cancelTargetPaymentNo(
								null
						)

						.build();


		// =========================
		// 지급내역 저장
		// =========================

		payrollPaymentDao.add(
				paymentDto
		);
	}

	@Override
	public void cancelPayment(
			int employeeNo,
			int payrollYear,
			int payrollMonth,
			long cancelAmount,
			String paymentNote) {

		// =========================
		// 급여 조회
		// =========================

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
		// 취소되지 않은 지급내역 조회
		// =========================

		List<PayrollPaymentDto> paymentList =
				payrollPaymentDao
						.findNotCancelledByPayroll(
								payrollNo
						);


		if (paymentList.isEmpty()) {
			throw new TargetNotfoundException();
		}


		// 현재 정책상 동시에 살아있는 지급은
		// 한 건만 허용
		if (paymentList.size() > 1) {
			throw new GetOutException();
		}


		PayrollPaymentDto targetPaymentDto =
				paymentList.get(0);


		if (!"paid".equals(
				targetPaymentDto.getPaymentStatus())) {

			throw new GetOutException();
		}


		// =========================
		// 취소금액 검증
		// =========================

		if (cancelAmount <= 0) {
			throw new GetOutException();
		}


		// =========================
		// 기존 취소내역 조회
		// =========================

		List<PayrollPaymentDto> paymentHistory =
				payrollPaymentDao
						.findAllByPayroll(
								payrollNo
						);


		long alreadyCancelledAmount = 0;


		for (PayrollPaymentDto paymentDto
				: paymentHistory) {

			if (!"cancelled".equals(
					paymentDto.getPaymentStatus())) {

				continue;
			}


			if (paymentDto.getCancelTargetPaymentNo() == null) {
				continue;
			}


			if (paymentDto.getCancelTargetPaymentNo()
					.equals(
							targetPaymentDto
									.getPayrollPaymentNo()
					)) {

				alreadyCancelledAmount +=
						paymentDto.getPaymentAmount();
			}
		}


		// =========================
		// 현재 취소 가능 금액
		// =========================

		long remainingAmount =
				targetPaymentDto.getPaymentAmount()
				- alreadyCancelledAmount;


		// 이미 전액 취소된 상태
		if (remainingAmount <= 0) {
			throw new GetOutException();
		}


		// 남은 지급액보다 많이 취소 불가
		if (cancelAmount > remainingAmount) {
			throw new GetOutException();
		}


		// =========================
		// 취소 이벤트 번호 생성
		// =========================

		long cancelPaymentNo =
				payrollPaymentDao.sequence();


		// =========================
		// 취소 이벤트 생성
		// =========================

		PayrollPaymentDto cancelPaymentDto =
				PayrollPaymentDto.builder()
						.payrollPaymentNo(
								cancelPaymentNo
						)

						.payrollNo(
								payrollNo
						)

						// 실제 취소하는 금액
						.paymentAmount(
								cancelAmount
						)

						.paymentAt(
								new Timestamp(
										System.currentTimeMillis()
								)
						)

						.paymentMethod(
								null
						)

						.paymentNote(
								paymentNote
						)

						.paymentStatus(
								"cancelled"
						)

						.cancelTargetPaymentNo(
								targetPaymentDto
										.getPayrollPaymentNo()
						)

						.build();


		payrollPaymentDao.add(
				cancelPaymentDto
		);
	}

	@Override
	public PayrollDetailResponseVO findDetail(
			int employeeNo,
			int payrollYear,
			int payrollMonth) {

		// =========================
		// 급여 조회
		// =========================

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
		// 공제 조회
		// =========================

		List<PayrollDeductionDto> deductionDtoList =
				payrollDeductionDao
						.findAllByPayroll(
								payrollNo
						);


		List<PayrollDeductionResponseVO> deductionList =
				new ArrayList<>();


		for (PayrollDeductionDto deductionDto
				: deductionDtoList) {

			PayrollDeductionResponseVO deductionVO =
					PayrollDeductionResponseVO.builder()
							.deductionType(
									deductionDto.getDeductionType()
							)
							.baseAmount(
									deductionDto.getBaseAmount()
							)
							.deductionRate(
									deductionDto.getDeductionRate()
							)
							.fixedDeductionAmount(
									deductionDto.getFixedDeductionAmount()
							)
							.deductionAmount(
									deductionDto.getDeductionAmount()
							)
							.deductionBasisYear(
									deductionDto.getDeductionBasisYear()
							)
							.deductionNote(
									deductionDto.getDeductionNote()
							)
							.build();


			deductionList.add(
					deductionVO
			);
		}


		// =========================
		// 지급 / 취소 이력 조회
		// =========================

		List<PayrollPaymentDto> paymentDtoList =
				payrollPaymentDao
						.findAllByPayroll(
								payrollNo
						);


		List<PayrollPaymentResponseVO> paymentList =
				new ArrayList<>();


		long paidAmount = 0;

		long cancelledAmount = 0;


		for (PayrollPaymentDto paymentDto
				: paymentDtoList) {

			PayrollPaymentResponseVO paymentVO =
					PayrollPaymentResponseVO.builder()
							.payrollPaymentNo(
									paymentDto.getPayrollPaymentNo()
							)
							.paymentAmount(
									paymentDto.getPaymentAmount()
							)
							.paymentAt(
									paymentDto.getPaymentAt()
							)
							.paymentMethod(
									paymentDto.getPaymentMethod()
							)
							.paymentNote(
									paymentDto.getPaymentNote()
							)
							.paymentStatus(
									paymentDto.getPaymentStatus()
							)
							.cancelTargetPaymentNo(
									paymentDto.getCancelTargetPaymentNo()
							)
							.build();


			paymentList.add(
					paymentVO
			);


			// =========================
			// 현재 지급금액 계산
			// =========================

			if ("paid".equals(
					paymentDto.getPaymentStatus())) {

				paidAmount +=
						paymentDto.getPaymentAmount();
			}


			else if ("cancelled".equals(
					paymentDto.getPaymentStatus())) {

				cancelledAmount +=
						paymentDto.getPaymentAmount();
			}
		}


		long currentPaidAmount =
				paidAmount
				- cancelledAmount;


		if (currentPaidAmount < 0) {
			throw new GetOutException();
		}


		// =========================
		// 급여 상세 응답 조립
		// =========================

		return PayrollDetailResponseVO.builder()
				.payrollNo(
						payrollDto.getPayrollNo()
				)
				.employeeNo(
						payrollDto.getEmployeeNo()
				)
				.payrollYear(
						payrollDto.getPayrollYear()
				)
				.payrollMonth(
						payrollDto.getPayrollMonth()
				)

				.totalWorkHours(
						payrollDto.getTotalWorkHours()
				)
				.totalOvertimeHours(
						payrollDto.getTotalOvertimeHours()
				)
				.totalNightHours(
						payrollDto.getTotalNightHours()
				)
				.totalHolidayHours(
						payrollDto.getTotalHolidayHours()
				)

				.basePay(
						payrollDto.getBasePay()
				)
				.weekHolidayPay(
						payrollDto.getWeekHolidayPay()
				)
				.overtimePay(
						payrollDto.getOvertimePay()
				)
				.nightPay(
						payrollDto.getNightPay()
				)
				.holidayPay(
						payrollDto.getHolidayPay()
				)

				.grossPay(
						payrollDto.getGrossPay()
				)
				.totalDeduction(
						payrollDto.getTotalDeduction()
				)
				.netPay(
						payrollDto.getNetPay()
				)

				.payrollStatus(
						payrollDto.getPayrollStatus()
				)
				.calculatedAt(
						payrollDto.getCalculatedAt()
				)
				.confirmedAt(
						payrollDto.getConfirmedAt()
				)

				.deductionList(
						deductionList
				)
				.paymentList(
						paymentList
				)

				.currentPaidAmount(
						currentPaidAmount
				)

				.build();
	}
	
	
	@Override
	public List<PayrollPaymentResponseVO> findPaymentHistory(
			int employeeNo,
			int payrollYear,
			int payrollMonth) {

		// =========================
		// 급여 조회
		// =========================

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
		// 지급 / 취소 이력 조회
		// =========================

		List<PayrollPaymentDto> paymentDtoList =
				payrollPaymentDao
						.findAllByPayroll(
								payrollNo
						);


		List<PayrollPaymentResponseVO> paymentList =
				new ArrayList<>();


		for (PayrollPaymentDto paymentDto
				: paymentDtoList) {

			PayrollPaymentResponseVO paymentVO =
					PayrollPaymentResponseVO.builder()
							.payrollPaymentNo(
									paymentDto.getPayrollPaymentNo()
							)
							.paymentAmount(
									paymentDto.getPaymentAmount()
							)
							.paymentAt(
									paymentDto.getPaymentAt()
							)
							.paymentMethod(
									paymentDto.getPaymentMethod()
							)
							.paymentNote(
									paymentDto.getPaymentNote()
							)
							.paymentStatus(
									paymentDto.getPaymentStatus()
							)
							.cancelTargetPaymentNo(
									paymentDto.getCancelTargetPaymentNo()
							)
							.build();


			paymentList.add(
					paymentVO
			);
		}


		return paymentList;
	}
	
	
	@Override
	public List<PayrollListResponseVO> findAllByEmployee(
			int employeeNo) {

		// =========================
		// 직원 급여 전체 조회
		// =========================

		List<PayrollDto> payrollDtoList =
				payrollDao.findAllByEmployee(
						employeeNo
				);


		List<PayrollListResponseVO> payrollList =
				new ArrayList<>();


		// 급여내역이 없어도
		// 목록 조회이므로 빈 List 반환
		if (payrollDtoList.isEmpty()) {

			return payrollList;
		}


		// =========================
		// 급여별 목록 응답 생성
		// =========================

		for (PayrollDto payrollDto
				: payrollDtoList) {

			long payrollNo =
					payrollDto.getPayrollNo();


			// =========================
			// 현재 지급금액 계산
			// =========================

			List<PayrollPaymentDto> paymentList =
					payrollPaymentDao
							.findAllByPayroll(
									payrollNo
							);


			long paidAmount = 0;

			long cancelledAmount = 0;


			for (PayrollPaymentDto paymentDto
					: paymentList) {

				if ("paid".equals(
						paymentDto.getPaymentStatus())) {

					paidAmount +=
							paymentDto.getPaymentAmount();
				}


				else if ("cancelled".equals(
						paymentDto.getPaymentStatus())) {

					cancelledAmount +=
							paymentDto.getPaymentAmount();
				}
			}


			long currentPaidAmount =
					paidAmount
					- cancelledAmount;


			// 지급취소 데이터가
			// 원 지급액보다 많으면 데이터 이상
			if (currentPaidAmount < 0) {

				throw new GetOutException();
			}


			// =========================
			// 목록 응답 생성
			// =========================

			PayrollListResponseVO payrollVO =
					PayrollListResponseVO.builder()
							.payrollNo(
									payrollDto.getPayrollNo()
							)

							.payrollYear(
									payrollDto.getPayrollYear()
							)

							.payrollMonth(
									payrollDto.getPayrollMonth()
							)

							.totalWorkHours(
									payrollDto.getTotalWorkHours()
							)

							.grossPay(
									payrollDto.getGrossPay()
							)

							.totalDeduction(
									payrollDto.getTotalDeduction()
							)

							.netPay(
									payrollDto.getNetPay()
							)

							.payrollStatus(
									payrollDto.getPayrollStatus()
							)

							.calculatedAt(
									payrollDto.getCalculatedAt()
							)

							.confirmedAt(
									payrollDto.getConfirmedAt()
							)

							.currentPaidAmount(
									currentPaidAmount
							)

							.build();


			payrollList.add(
					payrollVO
			);
		}


		return payrollList;
	}
	
	@Override
	public List<PayrollMonthlyListResponseVO> findAllByPeriod(
			int payrollYear,
			int payrollMonth) {

		// =========================
		// 연도 / 월 검증
		// =========================

		if (payrollYear <= 0) {
			throw new GetOutException();
		}


		if (payrollMonth < 1
				|| payrollMonth > 12) {

			throw new GetOutException();
		}


		// =========================
		// 조회기간 생성
		// =========================

		LocalDate start =
				LocalDate.of(
						payrollYear,
						payrollMonth,
						1
				);


		LocalDate end =
				start.plusMonths(1);


		Timestamp startDate =
				Timestamp.valueOf(
						start.atStartOfDay()
				);


		Timestamp endDate =
				Timestamp.valueOf(
						end.atStartOfDay()
				);


		// =========================
		// 해당 월 계약 직원 조회
		// =========================

		List<Integer> employeeNoList =
				contractDao.findEmployeeNoByPeriod(
						startDate,
						endDate
				);


		List<PayrollMonthlyListResponseVO> result =
				new ArrayList<>();


		// =========================
		// 직원별 급여 현황 조립
		// =========================

		for (int employeeNo : employeeNoList) {


			// =========================
			// 직원 정보 조회
			// =========================

			AdminEmployeeDetailVO employeeVO =
					employeeDao
							.selectAdminEmployeeDetailByEmployeeNo(
									employeeNo
							);


			if (employeeVO == null) {
				continue;
			}


			// =========================
			// 직원 급여 목록 조회
			// =========================

			List<PayrollDto> payrollList =
					payrollDao.findAllByEmployee(
							employeeNo
					);


			PayrollDto payrollDto = null;


			// =========================
			// 요청 연 / 월 급여 찾기
			// =========================

			for (PayrollDto findPayrollDto
					: payrollList) {


				if (findPayrollDto.getPayrollYear()
						== payrollYear

						&& findPayrollDto.getPayrollMonth()
						== payrollMonth) {


					payrollDto =
							findPayrollDto;


					break;
				}
			}


			// =========================
			// 기본 응답 생성
			// =========================

			PayrollMonthlyListResponseVO responseVO =
					PayrollMonthlyListResponseVO
							.builder()

							.employeeNo(
									employeeNo
							)

							.accountName(
									employeeVO
											.getAccountName()
							)

							.payrollYear(
									payrollYear
							)

							.payrollMonth(
									payrollMonth
							)

							.build();


			// =========================
			// 급여가 존재하는 경우
			// =========================

			if (payrollDto != null) {

				responseVO.setPayrollNo(
						payrollDto
								.getPayrollNo()
				);


				responseVO.setTotalWorkHours(
						payrollDto
								.getTotalWorkHours()
				);


				responseVO.setGrossPay(
						payrollDto
								.getGrossPay()
				);


				responseVO.setTotalDeduction(
						payrollDto
								.getTotalDeduction()
				);


				responseVO.setNetPay(
						payrollDto
								.getNetPay()
				);


				responseVO.setPayrollStatus(
						payrollDto
								.getPayrollStatus()
				);
			}


			result.add(
					responseVO
			);
		}


		return result;
	}
	
}

