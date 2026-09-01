package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="상담 정보 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultDto {
	private int consultNo;
	private int customerNo;
	private int consultEmployeeNo;
	private String consultTitle;
	private String consultContent;
	private Timestamp consultCtime;
}
