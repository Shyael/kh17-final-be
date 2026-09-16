package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleResponseVO {

    // 예정 근무 날짜
    private Timestamp scheduledWorkDate;

    // 예정 출근 시간
    private Timestamp scheduledClockIn;

    // 예정 퇴근 시간
    private Timestamp scheduledClockOut;

    // workday / holiday / dayOff
    private String scheduledDayType;


    // =========================
    // 예정근무 계산
    // =========================

    // 해당 날짜 예정 실근로시간
    private Double scheduledWorkHours;

    // 계약상 일 소정근로시간
    private Double dailyWorkHours;

    // 해당 날짜 소정근로 초과 예정시간
    private Double scheduledOvertimeHours;


    // =========================
    // 계약 주간 기준
    // =========================

    // 계약상 주 소정근로시간
    private Double weeklyWorkHours;


    // =========================
    // 실제 근무
    // =========================

    private double actualWorkHours;

    private double actualOvertimeHours;

    private double actualNightHours;

    private double actualHolidayHours;


    // =========================
    // 실제 근태
    // =========================

    private String attendanceType;

    private Timestamp clockIn;

    private Timestamp clockOut;

    private Long empAttendanceNo;

    private Double breakMinutes;


    // =========================
    // 식별값
    // =========================

    private Long workScheduleNo;

    private Long contractNo;
}