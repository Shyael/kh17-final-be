package com.kh.khedu.vo.message;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class MessageVO {
	private int no;
	private int room;
	private String type;
	private String content;
	private LocalDateTime time;
}