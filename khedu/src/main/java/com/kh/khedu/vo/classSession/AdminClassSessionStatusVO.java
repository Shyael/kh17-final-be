package com.kh.khedu.vo.classSession;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "관리자 강좌세션 정보수정VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminClassSessionStatusVO {
	private int sessionNo;
	private String sessionStatus; // 진행중, 종료, 취소
	private Integer classroomNo; // 변경할 강의실 번호
	private Timestamp sessionStart; // 변경할 시작시간 (선택)
	private Timestamp sessionEnd;// 변경할 종료시간 (선택)
}
