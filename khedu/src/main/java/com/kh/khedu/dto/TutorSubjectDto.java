
package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사과목 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorSubjectDto {
	private int tutorSubjectNo;
	private int tutorNo;
	private int academySubjectNo;
}
