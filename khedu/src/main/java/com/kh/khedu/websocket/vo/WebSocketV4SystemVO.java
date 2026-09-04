package com.kh.khedu.websocket.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//사용자에게 보내줄 데이터
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WebSocketV4SystemVO {
	@Builder.Default
	private String type = "system";
	private String content;
	private LocalDateTime time;
	
	private String level;
}