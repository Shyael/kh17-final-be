package com.kh.khedu.vo.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(name = "채팅방 기존 메세지 요청 데이터")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class RoomMessageRequestVO {
	@Positive
	private int size = 100;
	@Positive
	private Integer lastMessageNo;
}