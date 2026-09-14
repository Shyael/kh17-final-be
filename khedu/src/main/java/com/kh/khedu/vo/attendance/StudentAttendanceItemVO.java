package com.kh.khedu.vo.attendance;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생 - 본인 출결 조회")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentAttendanceItemVO {
	private int sessionNo; //세션 번호
	private int roundNo; // 몇 회차인지
	private Timestamp sessionStart; //수업 예정 시작 시간
	private Timestamp sessionEnd; // 수업 예정 종료 시각
	private Timestamp attendanceAt; //학생이 실제 태그한 입실 시각
	private String attendanceState; //출석, 지각, 조퇴, 결석, 미출결
}
