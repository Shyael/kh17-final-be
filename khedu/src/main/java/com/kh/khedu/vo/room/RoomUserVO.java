package com.kh.khedu.vo.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomUserVO {
	private Integer accountNo;
	private String accountName;
	private String accountType;
}
