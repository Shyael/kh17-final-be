package com.kh.khedu.vo.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name= "학생 개인정보 수정 요청")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeStudentRequestVO {	
	private String accountName;
	private String accountPhone;
	private String accountBirth;
	
	private String studentSchool;
	private String studentGrade;
}
