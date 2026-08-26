package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학원연혁 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AcademyHistoryDto {
	private int academyHistoryNo;
	private int academyNo;
	private String academyHistoryYear;
	private String academyHistoryContent;

}