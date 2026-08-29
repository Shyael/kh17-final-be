package com.kh.khedu.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // final + notNull
public enum RoleType {
	
	STUDENT(1, "STUDENT", "학생"),
	PARENT(2, "PARENT", "학부모"),
	TEACHER(3, "TEACHER", "강사"),
	DESK(4, "DESK", "데스크"),
	ADMIN(5,"ADMIN", "원장");
	
	private final int roleNo;
	private final String code;
	private final String description;
	
}
