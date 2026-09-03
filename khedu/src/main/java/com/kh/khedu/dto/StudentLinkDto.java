package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "부모자식연동 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentLinkDto {
	private int studentLinkNo;
	private int studentNo;
	private String linkCode;
	private Timestamp linkExpire;
	private String linkUsedYn; //사용여부 YN
	private Timestamp studentLinkCtime; // 연동코드 생성일 systimestamp
}
