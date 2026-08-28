package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "학생 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentDto {
	private int studentNo;
	private int accountNo;
	private String studentSchool;
	private String studentGrade;
	private String stduentGender;
	private String studentStatus;
	private String studentEtc;
}
