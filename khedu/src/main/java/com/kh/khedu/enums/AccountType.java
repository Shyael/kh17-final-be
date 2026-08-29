package com.kh.khedu.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // final + notNull
public enum AccountType {
	
	STUDENT("STUDENT", "학생"),
	PARENT("PARENT", "학부모"),
	EMPLOYEE("EMPLOYEE", "직원");
	
	private final String code;
	private final String description;
	
}
