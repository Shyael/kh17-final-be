package com.kh.khedu.dto.payroll;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="근태 기록 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceDto {
	   // 직원 근태 번호
    private long empAttendanceNo;

    // 근로계약 번호
    private long contractNo;

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
    private int breakMinutes;

    // 연장 근무시간
    private Double overtimeHours;

    // 야간 근무시간
    private Double nightHours;

    // 근태 구분
    // normal / absent / paid_leave / unpaid_leave
    private String attendanceType;
}
