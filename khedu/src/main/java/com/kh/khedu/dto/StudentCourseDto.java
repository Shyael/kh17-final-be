package com.kh.khedu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class StudentCourseDto {
	private int courseNo;
	private int studentNo;
	private String studentCourseStatus;
}
