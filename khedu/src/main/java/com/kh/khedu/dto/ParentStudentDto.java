package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "학부모-학생 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentStudentDto {
	//복합키 (parentNo, studentNo)
	private int parentNo;
	private int studentNo;  
	private String relationship; // 부 / 모 / 기타 / 보호자
}
