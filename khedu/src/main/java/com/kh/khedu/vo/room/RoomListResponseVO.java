package com.kh.khedu.vo.room;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "채팅방 목록 조회 결과")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomListResponseVO {
	private int count;
	private List<RoomListVO> rooms;
}