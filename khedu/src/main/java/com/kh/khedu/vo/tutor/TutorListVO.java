package com.kh.khedu.vo.tutor;

import com.kh.khedu.dto.AttachDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사 목록 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorListVO {
	
	private int tutorNo;
	private int employeeNo;
	private String accountName;//강사이름
	private String accountPhone;//강사전화번호
	private String tutorTagline;//강사 한줄소개
	
	//강사 이미지
	private AttachDto image;
}
