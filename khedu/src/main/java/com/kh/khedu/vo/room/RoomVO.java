package com.kh.khedu.vo.room;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomVO {
	private int roomNo;
	private int roomOwner;
	private String roomType;
	private Timestamp roomCtime;
	private int unreadCnt;
}
