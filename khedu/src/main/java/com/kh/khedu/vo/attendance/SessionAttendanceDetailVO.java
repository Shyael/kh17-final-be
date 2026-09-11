package com.kh.khedu.vo.attendance;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionAttendanceDetailVO {
    private int sessionNo;
    private int totalCount;       // 총 수강생 수
    private int presentCount;     // 출석 인원
    private int lateCount;        // 지각 인원
    private int earlyLeaveCount;  // 조퇴 인원
    private int absentCount;      // 결석 인원
    private int uncheckedCount;   // 미출결 인원
    private List<AttendanceStudentResponseVO> studentList; // 학생 목록
}