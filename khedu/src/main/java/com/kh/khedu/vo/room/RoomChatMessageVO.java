package com.kh.khedu.vo.room;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//message + message_chat 테이블에 접근하기 위한 VO
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomChatMessageVO {
	private int messageNo;
	private int messageRoom;
	private String messageType;
	private String messageContent;
	private int messageSenderNo;
	private String messageSenderName, messageSenderType;
	private Timestamp messageTime;
}