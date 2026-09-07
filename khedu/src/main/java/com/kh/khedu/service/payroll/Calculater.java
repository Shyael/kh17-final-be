package com.kh.khedu.service.payroll;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.dao.payroll.EmployeeWorkScheduleDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;
import com.kh.khedu.dto.payroll.EmployeeWorkScheduleDto;
import com.kh.khedu.dto.payroll.PayrollDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
@Component
public class Calculater {
	
	@Autowired
	private ContractDao contractDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private EmployeeAttendanceDao employeeAttendanceDao;
	
	@Autowired
	private EmployeeWorkScheduleDao employeeWorkScheduleDao;
	public PayrollDto calculatePayroll(
			long payrollNo,
			int employeeNo,
			int payrollYear,
			int payrollMonth) {

		// 네 현재 calculate()에 있는
		//
		// LocalDate start
		// LocalDate end
		// contractList
		// scheduleList
		// totalWorkHours
		// ...
		// basePay
		// overtimePay
		// nightPay
		// holidayPay
		// weekHolidayPay
		// grossPay
		//
		// 계산 부분을 여기로 이동

		
		
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
		
		// =========================
		// 주휴수당 계산
		// =========================

		// 직원 고용일자 조회
		Timestamp employmentDate =
				employeeDao.findEmploymentDate(employeeNo);

		if (employmentDate == null) {
			throw new TargetNotfoundException();
		}

		LocalDate hireDate =
				employmentDate
						.toLocalDateTime()
						.toLocalDate();


		// 급여월 첫 주는 이전 달 날짜가 포함될 수 있음
		LocalDate weekSearchStart =
				start.minusDays(6);

		Timestamp weekSearchStartDate =
				Timestamp.valueOf(
						weekSearchStart.atStartOfDay()
				);

		// 주휴 판단용 스케줄
		List<EmployeeWorkScheduleDto> weekScheduleList =
				employeeWorkScheduleDao.findByPeriod(
						employeeNo,
						weekSearchStartDate,
						endDate
				);


		// 급여월의 날짜를 하루씩 확인
		for (LocalDate holidayDate = start;
				holidayDate.isBefore(end);
				holidayDate = holidayDate.plusDays(1)) {


			// =========================
			// 현재 날짜에 적용되는 계약 찾기
			// =========================

			ContractDto holidayContract = null;

			for (ContractDto contractDto : contractList) {

				LocalDate contractStart =
						contractDto.getContractStart()
								.toLocalDateTime()
								.toLocalDate();

				LocalDate contractEnd = null;

				if (contractDto.getContractEnd() != null) {

					contractEnd =
							contractDto.getContractEnd()
									.toLocalDateTime()
									.toLocalDate();
				}


				boolean afterStart =
						!holidayDate.isBefore(contractStart);

				boolean beforeEnd =
						contractEnd == null
						|| !holidayDate.isAfter(contractEnd);


				if (afterStart && beforeEnd) {

					holidayContract = contractDto;

					break;
				}
			}


			// 해당 날짜에 계약이 없음
			if (holidayContract == null) {
				continue;
			}


			// =========================
			// 계약상 주휴일 확인
			// =========================

			String holidayDay =
					holidayDate
							.getDayOfWeek()
							.toString();

			String weeklyHolidayDay =
					holidayContract
							.getWeeklyHolidayDay();


			if (!holidayDay.equals(
					weeklyHolidayDay)) {

				continue;
			}


			// =========================
			// 월급제 제외
			// =========================

			// 현재 KH EDU 기준
			// 월급제는 월 기본급에 주휴임금이 포함된 것으로 처리
			if ("monthly".equals(
					holidayContract.getWageType())) {

				continue;
			}


			// =========================
			// 주 15시간 미만 제외
			// =========================

			if (holidayContract.getWeeklyWorkHours() == null
					|| holidayContract.getWeeklyWorkHours() < 15) {

				continue;
			}


			// =========================
			// 이번 주 범위
			// =========================

			// 주휴일 포함 직전 7일
			LocalDate weekStart =
					holidayDate.minusDays(6);

			LocalDate weekEnd =
					holidayDate;


			// =========================
			// 근로관계 유지 확인
			// =========================

			// 이번 주 시작 이후 입사했다면
			// 완전한 7일 근로관계가 아니므로 제외
			if (hireDate.isAfter(weekStart)) {

				continue;
			}


			// =========================
			// 소정근로일 개근 확인
			// =========================

			boolean hasWorkday = false;

			boolean perfectAttendance = true;

			boolean hasActualWork = false;


			for (EmployeeWorkScheduleDto scheduleDto
					: weekScheduleList) {

				LocalDate scheduleDate =
						scheduleDto
								.getScheduledWorkDate()
								.toLocalDateTime()
								.toLocalDate();


				// 이번 주 이전
				if (scheduleDate.isBefore(weekStart)) {
					continue;
				}


				// 이번 주 이후
				if (scheduleDate.isAfter(weekEnd)) {
					continue;
				}


				// 실제 소정근로일만 확인
				if (!"workday".equals(
						scheduleDto.getScheduledDayType())) {

					continue;
				}


				hasWorkday = true;


				// 네가 기존 basePay에서도 사용 중인
				// 현재 Attendance 조회 메소드 그대로 사용
				EmployeeAttendanceDto attendanceDto =
						employeeAttendanceDao
								.findBySchedule(
										scheduleDto
												.getWorkScheduleNo()
								);


				// 소정근로일인데 근태 자체가 없으면
				// 급여 계산 데이터가 아직 완성되지 않은 상태
				if (attendanceDto == null) {

					throw new GetOutException();
				}


				// 결근이면 개근 실패
				if ("absent".equals(
						attendanceDto.getAttendanceType())) {

					perfectAttendance = false;

					break;
				}


				// 정상근무를 실제 한 날이 있는지 확인
				if ("normal".equals(
						attendanceDto.getAttendanceType())) {

					if (scheduleDto.getActualWorkHours() != null
							&& scheduleDto.getActualWorkHours() > 0) {

						hasActualWork = true;
					}
				}


				// paid_leave
				// unpaid_leave
				//
				// 현재 KH EDU에서는 승인된 휴가로 취급
				// 개근 실패로 처리하지 않음
			}


			// 소정근로일 자체가 없음
			if (!hasWorkday) {
				continue;
			}


			// 결근 발생
			if (!perfectAttendance) {
				continue;
			}


			// 한 주 전체가 휴가 등으로
			// 실제 근무가 하나도 없음
			if (!hasActualWork) {
				continue;
			}


			// =========================
			// 주휴 통상시급 계산
			// =========================

			String wageType =
					holidayContract.getWageType();

			long baseWage =
					holidayContract.getBaseWage();

			double ordinaryHourlyWage = 0;


			// 시급제
			if ("hourly".equals(wageType)) {

				ordinaryHourlyWage =
						baseWage;
			}


			// 일급제
			else if ("daily".equals(wageType)) {

				if (holidayContract.getDailyWorkHours() == null
						|| holidayContract.getDailyWorkHours() <= 0) {

					throw new GetOutException();
				}


				ordinaryHourlyWage =
						(double) baseWage
						/ holidayContract.getDailyWorkHours();
			}


			else {

				throw new GetOutException();
			}


			// =========================
			// 주휴시간 계산
			// =========================

			double weekHolidayHours =
					holidayContract.getWeeklyWorkHours()
					/ 40.0
					* 8.0;


			// =========================
			// 주휴수당 계산
			// =========================

			double contractWeekHolidayPay =
					ordinaryHourlyWage
					* weekHolidayHours;


			weekHolidayPay +=
					Math.round(
							contractWeekHolidayPay
					);
		}
		
		// =========================
		// 총 지급액 계산
		// =========================

		long grossPay =
				basePay
				+ weekHolidayPay
				+ overtimePay
				+ nightPay
				+ holidayPay;

		return PayrollDto.builder()
				.payrollNo(payrollNo)
				.employeeNo(employeeNo)
				.payrollYear(payrollYear)
				.payrollMonth(payrollMonth)

				.totalWorkHours(totalWorkHours)
				.totalOvertimeHours(totalOvertimeHours)
				.totalNightHours(totalNightHours)
				.totalHolidayHours(totalHolidayHours)

				.basePay(basePay)
				.weekHolidayPay(weekHolidayPay)
				.overtimePay(overtimePay)
				.nightPay(nightPay)
				.holidayPay(holidayPay)

				.grossPay(grossPay)

				.totalDeduction(0L)
				.netPay(0L)

				.payrollStatus("calculating")
				.calculatedAt(null)
				.confirmedAt(null)

				.build();

		
	}
}
