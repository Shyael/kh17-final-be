package com.kh.khedu.vo.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class SystemMessageVO extends MessageVO{
	private int no;
	private String level;
}