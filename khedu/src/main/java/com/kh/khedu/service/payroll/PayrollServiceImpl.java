package com.kh.khedu.service.payroll;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.EmployeeWorkScheduleDao;
import com.kh.khedu.dao.payroll.PayrollDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.dto.payroll.EmployeeWorkScheduleDto;
import com.kh.khedu.dto.payroll.PayrollDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;

import java.time.temporal.ChronoUnit;

import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;

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

	@Override
	public void calculate(long employeeNo, int payrollYear, int payrollMonth) {

		// 이미 해당 월 급여가 존재하는지 확인
		PayrollDto findDto = payrollDao.findByEmployeeAndPeriod(employeeNo, payrollYear, payrollMonth);

		if (findDto != null) {
			throw new GetOutException();
		}

		// 급여 산정기간 시작일
		// ex) 2026년 9월 급여 -> 2026-09-01
		LocalDate start = LocalDate.of(payrollYear, payrollMonth, 1);

		// 다음 달 1일
		// ex) 2026-09-01 -> 2026-10-01
		// Mapper에서 endDate 미만(<)으로 조회하기 때문에
		// 실제 조회범위는 9/1 ~ 9/30
		LocalDate end = start.plusMonths(1);

		Timestamp startDate = Timestamp.valueOf(start.atStartOfDay());

		Timestamp endDate = Timestamp.valueOf(end.atStartOfDay());

		// 급여기간과 겹치는 계약 전체 조회
		List<ContractDto> contractList = contractDao.findListByEmployeeAndPeriod(employeeNo, startDate, endDate);

		// 해당 월에 적용되는 계약이 없으면 급여 계산 불가
		if (contractList.isEmpty()) {
			throw new TargetNotfoundException();
		}

		// 같은 급여기간의 근무스케줄 조회
		List<EmployeeWorkScheduleDto> scheduleList = employeeWorkScheduleDao.findByPeriod(employeeNo, startDate,
				endDate);

		// 월 전체 실제 근로시간 합계
		double totalWorkHours = 0;
		double totalOvertimeHours = 0;
		double totalNightHours = 0;
		double totalHolidayHours = 0;

		for (EmployeeWorkScheduleDto scheduleDto : scheduleList) {

			if (scheduleDto.getActualWorkHours() != null) {

				totalWorkHours += scheduleDto.getActualWorkHours();
			}

			if (scheduleDto.getActualOvertimeHours() != null) {

				totalOvertimeHours += scheduleDto.getActualOvertimeHours();
			}

			if (scheduleDto.getActualNightHours() != null) {

				totalNightHours += scheduleDto.getActualNightHours();
			}

			if (scheduleDto.getActualHolidayHours() != null) {

				totalHolidayHours += scheduleDto.getActualHolidayHours();
			}
		}

		// 계약별 급여 계산
		// 월 전체 기본급
		long basePay = 0;

		long overtimePay = 0;

		long nightPay = 0;
		
		long holidayPay = 0;
		
		long weekHolidayPay = 0;

		// 계약별 급여 계산
		for (ContractDto contractDto : contractList) {

			long contractNo = contractDto.getContractNo();

			// 현재 계약에 해당하는 근무스케줄만 담기
			List<EmployeeWorkScheduleDto> contractScheduleList = new ArrayList<>();

			for (EmployeeWorkScheduleDto scheduleDto : scheduleList) {

				if (scheduleDto.getContractNo() == contractNo) {

					contractScheduleList.add(scheduleDto);
				}
			}

			String wageType = contractDto.getWageType();

			long baseWage = contractDto.getBaseWage();

			// =========================
			// 월급제
			// =========================
			if ("monthly".equals(wageType)) {

				// 급여월 마지막 날짜
				LocalDate payrollEnd = end.minusDays(1);

				LocalDate contractStart = contractDto.getContractStart().toLocalDateTime().toLocalDate();

				LocalDate contractEnd = contractDto.getContractEnd() == null ? payrollEnd
						: contractDto.getContractEnd().toLocalDateTime().toLocalDate();

				// 급여월 시작일과 계약 시작일 중 더 늦은 날짜
				LocalDate appliedStart = contractStart.isAfter(start) ? contractStart : start;

				// 급여월 마지막날과 계약 종료일 중 더 빠른 날짜
				LocalDate appliedEnd = contractEnd.isBefore(payrollEnd) ? contractEnd : payrollEnd;

				if (appliedEnd.isBefore(appliedStart)) {
					continue;
				}

				// 해당 월에서 이 계약이 적용된 역일수
				long appliedDays = ChronoUnit.DAYS.between(appliedStart, appliedEnd) + 1;

				// 결근 / 무급휴가 일수
				long unpaidDays = 0;

				for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

					EmployeeAttendanceDto attendanceDto = employeeAttendanceDao
							.findBySchedule(scheduleDto.getWorkScheduleNo());

					if (attendanceDto == null) {
						continue;
					}

					if ("absent".equals(attendanceDto.getAttendanceType())
							|| "unpaid_leave".equals(attendanceDto.getAttendanceType())) {

						unpaidDays++;
					}
				}

				// 실제 급여 지급 대상 역일수
				long paidDays = appliedDays - unpaidDays;

				if (paidDays < 0) {
					paidDays = 0;
				}

				// 해당 월의 역일수
				int daysInMonth = start.lengthOfMonth();

				// 월급 ÷ 해당 월 역일수 × 지급 대상 역일수
				double contractBasePay = (double) baseWage / daysInMonth * paidDays;

				basePay += Math.round(contractBasePay);
			}

			// =========================
			// 시급제
			// =========================
			else if ("hourly".equals(wageType)) {

				double contractWorkHours = 0;

				for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

					if (scheduleDto.getActualWorkHours() != null) {

						contractWorkHours += scheduleDto.getActualWorkHours();
					}
				}

				// actualWorkHours 전체에 기본 시급 지급
				// 연장 / 야간 / 휴일 가산분은 이후 따로 계산
				double contractBasePay = baseWage * contractWorkHours;

				basePay += Math.round(contractBasePay);
			}

			// =========================
			// 일급제
			// =========================
			else if ("daily".equals(wageType)) {

				long paidDays = 0;

				for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

					EmployeeAttendanceDto attendanceDto = employeeAttendanceDao
							.findBySchedule(scheduleDto.getWorkScheduleNo());

					if (attendanceDto == null) {
						continue;
					}

					// 정상 근무
					if ("normal".equals(attendanceDto.getAttendanceType())) {

						paidDays++;
					}

					// 유급휴가도 지급 대상
					else if ("paid_leave".equals(attendanceDto.getAttendanceType())) {

						paidDays++;
					}
				}

				basePay += baseWage * paidDays;
			}

			else {
				throw new GetOutException();
			}

			// =========================
			// 2. 계약별 연장수당 계산
			// =========================

			// 현재 계약의 일반 근무일 연장시간 합계
			double contractOvertimeHours = 0;

			for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

				// 휴일 / 휴무일 연장은
				// overtimePay가 아니라 holidayPay에서 처리
				if ("holiday".equals(scheduleDto.getScheduledDayType())
						|| "dayOff".equals(scheduleDto.getScheduledDayType())) {

					continue;
				}

				if (scheduleDto.getActualOvertimeHours() != null) {

					contractOvertimeHours += scheduleDto.getActualOvertimeHours();
				}
			}

			// 통상시급
			double ordinaryHourlyWage = 0;

			// 시급제
			if ("hourly".equals(wageType)) {

				ordinaryHourlyWage = baseWage;
			}

			// 일급제
			else if ("daily".equals(wageType)) {

				if (contractDto.getDailyWorkHours() == null || contractDto.getDailyWorkHours() <= 0) {

					throw new GetOutException();
				}

				ordinaryHourlyWage = (double) baseWage / contractDto.getDailyWorkHours();
			}

			// 월급제
			else if ("monthly".equals(wageType)) {

				if (contractDto.getWeeklyWorkHours() == null || contractDto.getWeeklyWorkHours() <= 0) {

					throw new GetOutException();
				}

				// 현재 KH EDU 기준
				// 주 40시간 월급제는 월 통상임금 산정시간 209시간 사용
				if (contractDto.getWeeklyWorkHours() == 40) {

					ordinaryHourlyWage = (double) baseWage / 209;
				}

				else {

					throw new GetOutException();
				}
			}

			// 월급제 / 일급제는
			// 기존 basePay에 연장근무시간의 기본 1배가 포함되지 않으므로 추가
			if ("monthly".equals(wageType) || "daily".equals(wageType)) {

				double overtimeBasePay = ordinaryHourlyWage * contractOvertimeHours;

				basePay += Math.round(overtimeBasePay);
			}

			// overtimePay에는
			// 연장근무로 발생한 가산분 0.5만 저장
			double contractOvertimePay = ordinaryHourlyWage * contractOvertimeHours * 0.5;

			overtimePay += Math.round(contractOvertimePay);

			// =========================
			// 3. 계약별 야간수당 계산
			// =========================

			double contractNightHours = 0;

			for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

				if (scheduleDto.getActualNightHours() != null) {

					contractNightHours += scheduleDto.getActualNightHours();
				}
			}

			// nightPay에는
			// 야간근무로 발생한 가산분 0.5만 저장
			double contractNightPay = ordinaryHourlyWage * contractNightHours * 0.5;

			nightPay += Math.round(contractNightPay);

			// =========================
			// 계약별 휴일근로수당 계산
			// =========================

			for(EmployeeWorkScheduleDto scheduleDto
					: contractScheduleList) {

				// 휴일 / 휴무일 근무만 계산
				if(!"holiday".equals(
						scheduleDto.getScheduledDayType())
						&& !"dayOff".equals(
								scheduleDto.getScheduledDayType())) {

					continue;
				}


				// 실제 휴일근무시간이 없으면 계산하지 않음
				if(scheduleDto.getActualHolidayHours() == null
						|| scheduleDto.getActualHolidayHours() <= 0) {

					continue;
				}


				double holidayHours =
						scheduleDto.getActualHolidayHours();


				// =========================
				// 휴일근무 기본 1배 처리
				// =========================

				// 시급제는 actualWorkHours 전체를 이용해
				// 이미 basePay를 계산했으므로 기본 1배 추가하지 않음


				// 월급제는 휴일에 실제 근무한 시간의
				// 기본 1배가 basePay에 포함되어 있지 않으므로 추가
				if("monthly".equals(wageType)) {

					basePay +=
							Math.round(
									ordinaryHourlyWage
									* holidayHours
							);
				}


				// 일급제는 하루 일급에 dailyWorkHours까지의
				// 기본임금이 이미 포함되어 있으므로
				// dailyWorkHours 초과분만 기본 1배 추가
				else if("daily".equals(wageType)) {

					double extraBasicHours =
							holidayHours
							- contractDto.getDailyWorkHours();


					if(extraBasicHours > 0) {

						basePay +=
								Math.round(
										ordinaryHourlyWage
										* extraBasicHours
								);
					}
				}


				// =========================
				// 휴일근로 가산분 계산
				// =========================

				double contractHolidayPay = 0;


				// 휴일근로 8시간 이내
				if(holidayHours <= 8) {

					contractHolidayPay =
							ordinaryHourlyWage
							* holidayHours
							* 0.5;
				}


				// 휴일근로 8시간 초과
				else {

					// 최초 8시간은 0.5배 가산
					double firstEightHoursPay =
							ordinaryHourlyWage
							* 8
							* 0.5;


					// 8시간 초과분은 1.0배 가산
					double overEightHours =
							holidayHours - 8;


					double overEightHoursPay =
							ordinaryHourlyWage
							* overEightHours
							* 1.0;


					contractHolidayPay =
							firstEightHoursPay
							+ overEightHoursPay;
				}


				holidayPay +=
						Math.round(
								contractHolidayPay
						);
			}
		
		
		
		
		}

//		주휴 부터 해야 함
		
		
		
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
