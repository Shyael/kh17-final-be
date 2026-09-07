package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사정보 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorDto {
	private int tutorNo;
	private int employeeNo;
	private String tutorTagline;
	private String tutorIntro;
	private Timestamp tutorWtime;
	private Timestamp tutorEtime;
}
