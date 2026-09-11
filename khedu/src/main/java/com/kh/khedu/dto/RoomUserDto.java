package com.kh.khedu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomUserDto {
	private int roomUserNo;
	private int roomNo;
	private int accountNo;
}
