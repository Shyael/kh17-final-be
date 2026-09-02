package com.kh.khedu.vo.student;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학생 개인정보 수정 응답")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChangeStudentResponseVO {
	//student
	private String studentSchool;
	private String studentGrade;
	private Timestamp studentUtime; // 학생 정보 수정일
	
	// account
    private String accountName;
    private String accountPhone;
    private Timestamp accountUtime; // 학생 계정정보 수정일
}
