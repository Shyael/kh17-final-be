package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학원과목 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AcademySubjectDto {
	private int academySubjectNo;
	private int academyNo;
	private String academySubjectName;
}