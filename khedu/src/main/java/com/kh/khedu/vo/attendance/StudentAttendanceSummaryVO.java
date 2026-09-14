package com.kh.khedu.vo.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생 종합 통계 요약 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentAttendanceSummaryVO {
    private int totalSessionCount;   // 지금까지 진행된 총 세션 수
    private int presentCount;        // 출석
    private int lateCount;           // 지각
    private int earlyLeaveCount;     // 조퇴
    private int absentCount;         // 결석
    private int uncheckedCount; 	 // 미출결
    private double attendanceRate;   // 출석률 (%)
    
}
