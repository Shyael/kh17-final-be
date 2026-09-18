package com.kh.khedu.vo.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorWhenRegisterVO {
	private String scheduleWeek;
	private String scheduleStart;
	private String scheduleEnd;
}
