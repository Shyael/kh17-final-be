package com.kh.khedu.vo.parentStudent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "학부모 학생 관계 수정 요청")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ParentStudentRelatioshipUpdateRequestVO {

	//수정할 학생 번호
	private int studentNo;
	
	// 수정할 관계
	private String relationship;
}
