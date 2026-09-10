package com.kh.khedu.vo.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class ChatMessageVO extends MessageVO{
	private int no;
	private Integer senderNo;
	private String senderLevel;
	private String senderName;
}