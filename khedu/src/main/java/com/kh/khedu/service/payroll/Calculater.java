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
import com.kh.khedu.vo.admin.employee.AdminEmployeeDetailVO;
import com.kh.khedu.vo.payroll.response.ContractFindOrdinaryEmployeeVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public PayrollDto calculatePayroll(long payrollNo, int employeeNo, int payrollYear, int payrollMonth) {

		// =========================
		// 급여 산정기간
		// =========================

		LocalDate start = LocalDate.of(payrollYear, payrollMonth, 1);

		LocalDate end = start.plusMonths(1);

		Timestamp startDate = Timestamp.valueOf(start.atStartOfDay());

		Timestamp endDate = Timestamp.valueOf(end.atStartOfDay());

		// =========================
		// 고용일자
		// =========================

		Timestamp employmentDate = employeeDao.findEmploymentDate(employeeNo);

		if (employmentDate == null) {

			throw new TargetNotfoundException();
		}

		LocalDate hireDate = employmentDate.toLocalDateTime().toLocalDate();

		// =========================
		// 급여기간 계약
		// =========================

		List<ContractDto> contractList = contractDao.findListByEmployeeAndPeriod(employeeNo, startDate, endDate);

		if (contractList.isEmpty()) {

			throw new TargetNotfoundException();
		}

		// =========================
		// 급여기간 스케줄
		// =========================

		List<EmployeeWorkScheduleDto> scheduleList = employeeWorkScheduleDao.findByPeriod(employeeNo, startDate,
				endDate);

		// =========================
		// 월 전체 실제 근로시간
		// =========================

		double totalWorkHours = 0;
		double totalOvertimeHours = 0;
		double totalNightHours = 0;
		double totalHolidayHours = 0;

		// 실제시간은 주간 한도로 제한하지 않고 급여월 전체를 합산한다.
		// null은 기존 계약별 계산과 동일하게 합산에서 제외한다.
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

		// =========================
		// 급여 항목
		// =========================

		long basePay = 0;

		long weekHolidayPay = 0;

		long overtimePay = 0;

		long nightPay = 0;

		long holidayPay = 0;

		// =========================
		// 계약별 급여 계산
		// =========================

		for (ContractDto contractDto : contractList) {

			long contractNo = contractDto.getContractNo();

			List<EmployeeWorkScheduleDto> contractScheduleList = new ArrayList<>();

			for (EmployeeWorkScheduleDto scheduleDto : scheduleList) {

				if (scheduleDto.getContractNo() == contractNo) {

					contractScheduleList.add(scheduleDto);
				}
			}

			String wageType = contractDto.getWageType();

			long baseWage = contractDto.getBaseWage();

			// =========================
			// 1. 기본급
			// =========================

			// =========================
			// 월급제
			// =========================

			if ("monthly".equals(wageType)) {

				LocalDate payrollEnd = end.minusDays(1);

				LocalDate contractStart = contractDto.getContractStart().toLocalDateTime().toLocalDate();

				LocalDate contractEnd = contractDto.getContractEnd() == null ? payrollEnd
						: contractDto.getContractEnd().toLocalDateTime().toLocalDate();

				LocalDate appliedStart = contractStart.isAfter(start) ? contractStart : start;

				LocalDate appliedEnd = contractEnd.isBefore(payrollEnd) ? contractEnd : payrollEnd;

				if (appliedEnd.isBefore(appliedStart)) {

					continue;
				}

				long appliedDays = ChronoUnit.DAYS.between(appliedStart, appliedEnd) + 1;

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

				long paidDays = appliedDays - unpaidDays;

				if (paidDays < 0) {

					paidDays = 0;
				}

				int daysInMonth = start.lengthOfMonth();

				double contractBasePay = (double) baseWage / daysInMonth * paidDays;

				basePay += Math.round(contractBasePay);

				log.debug("[기본급 계산] contractNo={}, wageType={}, baseWage={}, contractBasePay={}",
						contractDto.getContractNo(), wageType, baseWage, contractBasePay);
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

				double contractBasePay = baseWage * contractWorkHours;

				basePay += Math.round(contractBasePay);

				log.debug("[기본급 계산] contractNo={}, wageType={}, baseWage={}, contractBasePay={}",
						contractDto.getContractNo(), wageType, baseWage, contractBasePay);
			}

			// =========================
// 일급제
// =========================

			else if ("daily".equals(wageType)) {

				if (contractDto.getDailyWorkHours() == null || contractDto.getDailyWorkHours() <= 0) {

					throw new GetOutException();
				}

				double dailyWorkHours = contractDto.getDailyWorkHours();

				double contractBasePay = 0;

				for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

					EmployeeAttendanceDto attendanceDto = employeeAttendanceDao
							.findBySchedule(scheduleDto.getWorkScheduleNo());

					if (attendanceDto == null) {

						continue;
					}

					String attendanceType = attendanceDto.getAttendanceType();

					// =========================
					// 유급휴가
					// 일급 전액 지급
					// =========================

					if ("paid_leave".equals(attendanceType)) {

						contractBasePay += baseWage;

						continue;
					}

					// =========================
					// 결근 / 무급휴가
					// 기본급 지급 없음
					// =========================

					if ("absent".equals(attendanceType) || "unpaid_leave".equals(attendanceType)) {

						continue;
					}

					// =========================
					// 정상근무
					// 실제 근로시간 비례 계산
					// =========================

					if ("normal".equals(attendanceType)) {

						if (scheduleDto.getActualWorkHours() == null || scheduleDto.getActualWorkHours() <= 0) {

							continue;
						}

						double actualWorkHours = scheduleDto.getActualWorkHours();

						// 일급 기본급은
						// 소정근로시간까지만 계산
						// 초과시간은 연장근로수당에서 계산
						double basicWorkHours = Math.min(actualWorkHours, dailyWorkHours);

						double paidRatio = basicWorkHours / dailyWorkHours;

						double dailyBasePay = baseWage * paidRatio;

						contractBasePay += dailyBasePay;

						log.debug(
								"[일급 기본급 계산] contractNo={}, baseWage={}, dailyWorkHours={}, actualWorkHours={}, basicWorkHours={}, paidRatio={}, dailyBasePay={}",
								contractDto.getContractNo(), baseWage, dailyWorkHours, actualWorkHours, basicWorkHours,
								paidRatio, dailyBasePay);
					}
				}

				basePay += Math.round(contractBasePay);

				log.debug("[기본급 계산] contractNo={}, wageType={}, baseWage={}, contractBasePay={}",
						contractDto.getContractNo(), wageType, baseWage, contractBasePay);
			}

			else {

				throw new GetOutException();
			}

			// =========================
			// 통상시급 계산
			// =========================

			double ordinaryHourlyWage = 0;

			if ("hourly".equals(wageType)) {

				ordinaryHourlyWage = baseWage;
			}

			else if ("daily".equals(wageType)) {

				if (contractDto.getDailyWorkHours() == null || contractDto.getDailyWorkHours() <= 0) {

					throw new GetOutException();
				}

				ordinaryHourlyWage = (double) baseWage / contractDto.getDailyWorkHours();
			}

			else if ("monthly".equals(wageType)) {

				if (contractDto.getWeeklyWorkHours() == null || contractDto.getWeeklyWorkHours() <= 0) {

					throw new GetOutException();
				}

				double monthlyScheduledWorkHours = contractDto.getWeeklyWorkHours() * 365.0 / 7.0 / 12.0;

				if (monthlyScheduledWorkHours <= 0) {

					throw new GetOutException();
				}

				ordinaryHourlyWage = (double) baseWage / monthlyScheduledWorkHours;
			}

			else {

				throw new GetOutException();
			}

			// =========================
			// 2. 연장근로수당
			// =========================

			double contractOvertimeHours = 0;

			for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

				// 휴일 / 휴무일은
				// 휴일근로수당에서 계산
				if ("holiday".equals(scheduleDto.getScheduledDayType())
						|| "dayOff".equals(scheduleDto.getScheduledDayType())) {

					continue;
				}

				if (scheduleDto.getActualOvertimeHours() != null) {

					contractOvertimeHours += scheduleDto.getActualOvertimeHours();
				}
			}

			// 월급 / 일급은
			// 연장근로 기본 1배 추가
			if ("monthly".equals(wageType) || "daily".equals(wageType)) {

				double overtimeBasePay = ordinaryHourlyWage * contractOvertimeHours;

				basePay += Math.round(overtimeBasePay);
			}

			// 연장 가산 0.5배
			double contractOvertimePay = ordinaryHourlyWage * contractOvertimeHours * 0.5;

			overtimePay += Math.round(contractOvertimePay);

			// =========================
			// 3. 야간근로수당
			// =========================

			double contractNightHours = 0;

			for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

				if (scheduleDto.getActualNightHours() != null) {

					contractNightHours += scheduleDto.getActualNightHours();
				}
			}

			double contractNightPay = ordinaryHourlyWage * contractNightHours * 0.5;

			nightPay += Math.round(contractNightPay);

			// =========================
			// 4. 휴일근로수당
			// =========================

			for (EmployeeWorkScheduleDto scheduleDto : contractScheduleList) {

				if (!"holiday".equals(scheduleDto.getScheduledDayType())
						&& !"dayOff".equals(scheduleDto.getScheduledDayType())) {

					continue;
				}

				if (scheduleDto.getActualHolidayHours() == null || scheduleDto.getActualHolidayHours() <= 0) {

					continue;
				}

				double holidayHours = scheduleDto.getActualHolidayHours();

				// =========================
				// 휴일근로 기본 1배
				// =========================

				// 시급제는 actualWorkHours에
				// 이미 기본 1배 포함

				// 월급제
				if ("monthly".equals(wageType)) {

					basePay += Math.round(ordinaryHourlyWage * holidayHours);
				}

				// 일급제
				else if ("daily".equals(wageType)) {

					double extraBasicHours = holidayHours - contractDto.getDailyWorkHours();

					if (extraBasicHours > 0) {

						basePay += Math.round(ordinaryHourlyWage * extraBasicHours);
					}
				}

				// =========================
				// 휴일 가산
				// =========================

				double contractHolidayPay = 0;

				if (holidayHours <= 8) {

					contractHolidayPay = ordinaryHourlyWage * holidayHours * 0.5;
				}

				else {

					double firstEightHoursPay = ordinaryHourlyWage * 8 * 0.5;

					double overEightHours = holidayHours - 8;

					double overEightHoursPay = ordinaryHourlyWage * overEightHours * 1.0;

					contractHolidayPay = firstEightHoursPay + overEightHoursPay;
				}

				holidayPay += Math.round(contractHolidayPay);
			}
		}

		// ============================================
		// 5. 주휴수당
		// ============================================

		AdminEmployeeDetailVO employeeVO = employeeDao.selectAdminEmployeeDetailByEmployeeNo(employeeNo);

		if (employeeVO == null) {

			throw new TargetNotfoundException();
		}

		// 급여월 첫 주 판단을 위해
		// 이전 6일까지 조회
		LocalDate weekSearchStart = start.minusDays(6);

		Timestamp weekSearchStartDate = Timestamp.valueOf(weekSearchStart.atStartOfDay());

		List<EmployeeWorkScheduleDto> weekScheduleList = employeeWorkScheduleDao.findByPeriod(employeeNo,
				weekSearchStartDate, endDate);

		// =========================
		// 급여월 날짜 하루씩 확인
		// =========================

		for (LocalDate weekHolidayDate = start; weekHolidayDate
				.isBefore(end); weekHolidayDate = weekHolidayDate.plusDays(1)) {

			// =========================
			// 해당 날짜 적용 계약
			// =========================

			ContractDto holidayContract = null;

			for (ContractDto contractDto : contractList) {

				if (contractDto.getWeeklyHolidayDay() == null) {

					continue;
				}

				LocalDate contractStart = contractDto.getContractStart().toLocalDateTime().toLocalDate();

				LocalDate contractEnd = null;

				if (contractDto.getContractEnd() != null) {

					contractEnd = contractDto.getContractEnd().toLocalDateTime().toLocalDate();
				}

				boolean afterStart = !weekHolidayDate.isBefore(contractStart);

				boolean beforeEnd = contractEnd == null || !weekHolidayDate.isAfter(contractEnd);

				if (afterStart && beforeEnd) {

					holidayContract = contractDto;

					break;
				}
			}

			if (holidayContract == null) {

				continue;
			}

			// =========================
			// 계약상 주휴요일
			// =========================

			String holidayDay = weekHolidayDate.getDayOfWeek().toString();

			String weeklyHolidayDay = holidayContract.getWeeklyHolidayDay();

			if (!holidayDay.equals(weeklyHolidayDay)) {

				continue;
			}

			// =========================
			// 이번 주 범위
			// =========================

			LocalDate weekStart = weekHolidayDate.minusDays(6);

			LocalDate weekEnd = weekHolidayDate;

			// =========================
			// 근로관계 유지
			// =========================

			if (hireDate.isAfter(weekStart)) {

				continue;
			}

			// =========================
			// 소정근로일 개근
			// =========================

			boolean hasWorkday = false;

			boolean perfectAttendance = true;

			boolean hasActualWork = false;

			for (EmployeeWorkScheduleDto scheduleDto : weekScheduleList) {

				LocalDate scheduleDate = scheduleDto.getScheduledWorkDate().toLocalDateTime().toLocalDate();

				if (scheduleDate.isBefore(weekStart)) {

					continue;
				}

				if (scheduleDate.isAfter(weekEnd)) {

					continue;
				}

				if (!"workday".equals(scheduleDto.getScheduledDayType())) {

					continue;
				}

				hasWorkday = true;

				EmployeeAttendanceDto attendanceDto = employeeAttendanceDao
						.findBySchedule(scheduleDto.getWorkScheduleNo());

				if (attendanceDto == null) {

					throw new GetOutException();
				}

				// 결근
				if ("absent".equals(attendanceDto.getAttendanceType())) {

					perfectAttendance = false;

					break;
				}

				// 실제 정상근무가 하나라도 있는지
				if ("normal".equals(attendanceDto.getAttendanceType())) {

					if (scheduleDto.getActualWorkHours() != null && scheduleDto.getActualWorkHours() > 0) {

						hasActualWork = true;
					}
				}

				// paid_leave / unpaid_leave
				// 현재 프로젝트에서는
				// 승인된 휴가이므로 개근 실패 처리하지 않음
			}

			if (!hasWorkday) {

				continue;
			}

			if (!perfectAttendance) {

				continue;
			}

			if (!hasActualWork) {

				continue;
			}

			// =========================
			// 4주 평균 소정근로시간
			// =========================

			LocalDate fourWeekStart = weekHolidayDate.minusDays(28);

			// 4주 미만 근로자라면
			// 입사일을 산정 시작일로 사용
			LocalDate calculationStart = hireDate.isAfter(fourWeekStart) ? hireDate : fourWeekStart;

			LocalDate fourWeekEnd = weekHolidayDate;

			Timestamp calculationStartDate = Timestamp.valueOf(calculationStart.atStartOfDay());

			Timestamp fourWeekEndDate = Timestamp.valueOf(fourWeekEnd.atStartOfDay());

			double periodScheduledWorkHours = employeeWorkScheduleDao.sumScheduledWorkHoursByPeriod(employeeNo,
					calculationStartDate, fourWeekEndDate);

			long calculationDays = ChronoUnit.DAYS.between(calculationStart, fourWeekEnd);

			if (calculationDays <= 0) {

				continue;
			}

			double calculationWeeks = calculationDays / 7.0;

			if (calculationWeeks <= 0) {

				continue;
			}

			double averageWeeklyScheduledWorkHours = periodScheduledWorkHours / calculationWeeks;

			if (averageWeeklyScheduledWorkHours < 15) {

				continue;
			}

			// =========================
			// 동종 업무 통상근로자 후보
			// =========================

			Timestamp targetDate = Timestamp.valueOf(weekHolidayDate.atStartOfDay());

			List<ContractFindOrdinaryEmployeeVO> ordinaryEmployeeList = contractDao
					.findOrdinaryEmployeeNoList(employeeVO.getEmployeeType(), targetDate);

			if (ordinaryEmployeeList.isEmpty()) {

				throw new TargetNotfoundException();
			}

			// =========================
			// 주휴용 통상시급
			// =========================

			String wageType = holidayContract.getWageType();

			long baseWage = holidayContract.getBaseWage();

			double ordinaryHourlyWage = 0;

			// 시급
			if ("hourly".equals(wageType)) {

				ordinaryHourlyWage = baseWage;
			}

			// 일급
			else if ("daily".equals(wageType)) {

				if (holidayContract.getDailyWorkHours() == null || holidayContract.getDailyWorkHours() <= 0) {

					throw new GetOutException();
				}

				ordinaryHourlyWage = (double) baseWage / holidayContract.getDailyWorkHours();
			}

			// 월급
			else if ("monthly".equals(wageType)) {

				if (holidayContract.getWeeklyWorkHours() == null || holidayContract.getWeeklyWorkHours() <= 0) {

					throw new GetOutException();
				}

				double monthlyScheduledWorkHours = holidayContract.getWeeklyWorkHours() * 365.0 / 7.0 / 12.0;

				if (monthlyScheduledWorkHours <= 0) {

					throw new GetOutException();
				}

				ordinaryHourlyWage = (double) baseWage / monthlyScheduledWorkHours;
			}

			else {

				throw new GetOutException();
			}

			// =========================
			// 통상근로자 기준
			// =========================

			int ordinaryScheduledWorkDays = 0;

			double ordinaryWeeklyWorkHours = 0;

			for (ContractFindOrdinaryEmployeeVO ordinaryEmployee : ordinaryEmployeeList) {

				int scheduledWorkDays = employeeWorkScheduleDao.countScheduledWorkDaysByPeriod(
						ordinaryEmployee.getEmployeeNo(), calculationStartDate, fourWeekEndDate);

				double formalWeeklyWorkHours = ordinaryEmployee.getWeeklyWorkHours();

				if (scheduledWorkDays <= 0) {

					continue;
				}

				if (formalWeeklyWorkHours <= 0) {

					continue;
				}

				if (scheduledWorkDays > ordinaryScheduledWorkDays) {

					ordinaryScheduledWorkDays = scheduledWorkDays;
				}

				if (formalWeeklyWorkHours > ordinaryWeeklyWorkHours) {

					ordinaryWeeklyWorkHours = formalWeeklyWorkHours;
				}
			}

			if (ordinaryScheduledWorkDays <= 0) {

				throw new TargetNotfoundException();
			}

			if (ordinaryWeeklyWorkHours <= 0) {

				throw new TargetNotfoundException();
			}

			// =========================
			// 대상 직원 주 소정근로시간
			// =========================

			if (holidayContract.getWeeklyWorkHours() == null || holidayContract.getWeeklyWorkHours() <= 0) {

				throw new GetOutException();
			}

			double targetWeeklyWorkHours = holidayContract.getWeeklyWorkHours();

			boolean isPartTimerTarget = targetWeeklyWorkHours < ordinaryWeeklyWorkHours;

			boolean isOrdinaryWorkerTarget = targetWeeklyWorkHours == ordinaryWeeklyWorkHours;

			// 통상근로자 최대시간보다
			// 대상 직원 시간이 더 클 수 없음
			if (targetWeeklyWorkHours > ordinaryWeeklyWorkHours) {

				throw new GetOutException();
			}

			// =========================
			// 단시간근로자 주휴
			// =========================

			if (isPartTimerTarget) {

				double dailyScheduledWorkHours = periodScheduledWorkHours / ordinaryScheduledWorkDays;

				double contractWeekHolidayPay = ordinaryHourlyWage * dailyScheduledWorkHours;

				weekHolidayPay += Math.round(contractWeekHolidayPay);
			}

			// =========================
			// 통상근로자 주휴
			// =========================

			if (isOrdinaryWorkerTarget) {

				if (holidayContract.getDailyWorkHours() == null || holidayContract.getDailyWorkHours() <= 0) {

					throw new GetOutException();
				}

				double dailyScheduledWorkHours = holidayContract.getDailyWorkHours();

				double contractWeekHolidayPay = ordinaryHourlyWage * dailyScheduledWorkHours;

				weekHolidayPay += Math.round(contractWeekHolidayPay);
			}
		}

		// =========================
		// 총 지급액
		// =========================

		long grossPay = basePay + weekHolidayPay + overtimePay + nightPay + holidayPay;

		// =========================
		// PayrollDto
		// =========================

		return PayrollDto.builder().payrollNo(payrollNo).employeeNo(employeeNo).payrollYear(payrollYear)
				.payrollMonth(payrollMonth)

				.totalWorkHours(totalWorkHours).totalOvertimeHours(totalOvertimeHours).totalNightHours(totalNightHours)
				.totalHolidayHours(totalHolidayHours)

				.basePay(basePay).weekHolidayPay(weekHolidayPay).overtimePay(overtimePay).nightPay(nightPay)
				.holidayPay(holidayPay)

				.grossPay(grossPay)

				.totalDeduction(0L).netPay(0L)

				.payrollStatus("calculating").calculatedAt(null).confirmedAt(null)

				.build();
	}
}