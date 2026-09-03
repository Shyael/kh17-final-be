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
public class WorkScheduleAddResponseVO {
	// 예정 근무 날짜
    private Timestamp scheduledWorkDate;

    // 예정 출근 시간
    private Timestamp scheduledClockIn;

    // 예정 퇴근 시간
    private Timestamp scheduledClockOut;

    // 예정 근무일 구분
    // workday / holiday / dayOff
    private String scheduledDayType;
    
    private long contractNo;
    
}
