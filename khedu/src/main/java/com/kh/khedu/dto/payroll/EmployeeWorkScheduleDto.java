package com.kh.khedu.dto.payroll;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWorkScheduleDto {

    // 근무 일정 번호
    private long workScheduleNo;

    // 적용 근로계약 번호
    private long contractNo;

    // 예정 근무 날짜
    private Timestamp scheduledWorkDate;

    // 예정 출근 시간
    private Timestamp scheduledClockIn;

    // 예정 퇴근 시간
    private Timestamp scheduledClockOut;

    // 예정 근무일 구분
    // workday / holiday / dayOff
    private String scheduledDayType;

    // 실제 근무 시간
    private double actualWorkHours;

    // 실제 연장 근무 시간
    private double actualOvertimeHours;

    // 실제 야간 근무 시간
    private double actualNightHours;

    // 실제 휴일 근무 시간
    private double actualHolidayHours;
}