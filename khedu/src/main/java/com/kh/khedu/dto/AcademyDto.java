package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학원정보 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AcademyDto {
	private int academyNo;
	private String academyName;
	private String academyTagline;
	private String academyIntro;
	private String academyPhone;
	private String academyAddress;
	private Timestamp academyWtime;
	private Timestamp academyEtime;
}
