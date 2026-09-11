package com.kh.khedu.vo.attendance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "키오스크 학생 출석 확인 응답VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KioskAttendanceResponseVO {
	// 결과 유형: "SUCCESS"(출결 완료), "MULTIPLE_CANDIDATES"(동일 뒷자리 선택 필요)
    private String actionType; //fe에 보내줄 신호 표시
	private String studentName; // account_name
	private String courseTitle; //출석 처리된 강좌명
	private String attendanceState; //출석 or 지각
	private String attendanceTime; //attendance_at
	private String message;
    private List<KioskStudentCandidateVO> candidateList;
}
