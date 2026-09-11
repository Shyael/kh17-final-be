package com.kh.khedu.vo.room;

import java.util.List;

import com.kh.khedu.vo.message.MessageVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "채팅방 상세 조회 데이터")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomDetailResponseVO {
	private RoomVO room;//방 정보
	private List<RoomUserVO> users;//참여자 정보
	private List<MessageVO> history;//과거채팅내역
}