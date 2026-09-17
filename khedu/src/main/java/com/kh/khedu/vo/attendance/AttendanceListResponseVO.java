package com.kh.khedu.vo.attendance;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강사 - 학생 출결 목록 응답 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceListResponseVO {
	private Integer attendanceNo;        // 출결 고유 번호
    private Integer sessionNo;           // 수업 세션 번호
    private String courseTitle;       // 강의명
    private String courseSubject;     // 과목명
    private Integer studentNo;           // 학생 번호
    private String studentName;       // 학생명
    private LocalDateTime sessionStart; // 수업 시작 일시
    private LocalDateTime sessionEnd;   // 수업 종료 일시
    private String sessionStatus;     // 수업 진행 상태
    private LocalDateTime attendanceAt; // 출결 체크 일시
    private String attendanceState;   // 출결 상태 ('미출결', '출석', '결석', '지각', '조퇴')
}
