package com.kh.khedu.vo.payroll;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Schema(name="근로계약과 근태")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAttendanceVO {

	// 근로계약 번호
	private long contractNo;
	// 1일 소정근로시간
	private int dailyWorkHours;

	// 급여 형태
	// monthly / hourly / daily
	private String wageType;

	// 기준 임금
	private long baseWage;

	// 직원 근태 번호
	private long empAttendanceNo;

	// 근무 날짜
	private Timestamp workDate;

	// 근무일 구분
	// weekday / holiday / day_off
	private String workDayType;

	// 출근 시각
	private Timestamp clockIn;

	// 퇴근 시각
	private Timestamp clockOut;

	// 휴게 시간(분)
	private int writtenBreakMinutes;

	// 연장 근무시간
	private Integer overtimeHours;

	// 야간 근무시간
	private Integer nightHours;

	// 근태 구분
	// normal / absent / paid_leave / unpaid_leave
	private String attendanceType;
}
