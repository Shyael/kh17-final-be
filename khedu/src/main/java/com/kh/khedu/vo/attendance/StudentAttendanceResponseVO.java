package com.kh.khedu.vo.attendance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생 - 본인 출석 정보 전체 목록 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentAttendanceResponseVO {
	// 1. 강좌 기본 정보
    private int courseNo;
    private String courseTitle;
    
    // 2. 출결 종합 요약 통계 (대시보드 / 상단 프로그레스 바용)
    private int totalSessionCount;   // 지금까지 진행된 총 세션 수
    private int presentCount;        // 출석
    private int lateCount;           // 지각
    private int earlyLeaveCount;     // 조퇴
    private int absentCount;         // 결석
    private double attendanceRate;   // 출석률 (%)
    
    // 3. 일자별 상세 출결 이력 목록
    private List<StudentAttendanceItemVO> attendanceList;
}
