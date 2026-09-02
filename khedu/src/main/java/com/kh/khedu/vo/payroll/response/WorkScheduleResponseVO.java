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
    
    //휴가인지 결근인지..
    private String attendanceType;
    
    private Timestamp clockIn;
    
    private Timestamp clockOut;
    
}