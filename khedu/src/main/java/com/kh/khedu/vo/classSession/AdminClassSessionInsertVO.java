package com.kh.khedu.vo.classSession;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "관리자 강좌세션 등록VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminClassSessionInsertVO {
	private int scheduleNo;
	private LocalDate targetDate;
	private String sessionStatus;
}
