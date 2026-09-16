package com.kh.khedu.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "방 참여 결과 데이터")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomEnterResponseVO {
	private boolean result;
	private String message;
}