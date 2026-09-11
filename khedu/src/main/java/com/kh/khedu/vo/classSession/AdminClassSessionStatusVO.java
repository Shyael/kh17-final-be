package com.kh.khedu.vo.classSession;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "관리자 강좌세션 상태수정VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminClassSessionStatusVO {
	private int sessionNo;
	private String sessionStatus;
}
