
package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사 학력 및 경력 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorCareerDto {
	private int tutorCareerNo;
	private int tutorNo;
	private String tutorCareerType;
	private String tutorCareerContent;
}
