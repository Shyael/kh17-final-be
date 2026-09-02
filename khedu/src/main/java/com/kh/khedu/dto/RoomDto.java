package com.kh.khedu.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomDto {
	private int roomNo;
	private int roomOwner;
	private Timestamp roomCtime;
}
