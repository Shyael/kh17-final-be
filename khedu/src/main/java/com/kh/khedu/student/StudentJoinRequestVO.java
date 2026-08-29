package com.kh.khedu.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name="학생 회원가입 요청")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class StudentJoinRequestVO {
	private String accountId;
	private String accountPassword;
	private String accountName;
	private String accountPhone;
	private String accountBirth;
	
	private String studentSchool;
	private String studentGrade;
	private String studentGender;
	private String studentEtc;
}
