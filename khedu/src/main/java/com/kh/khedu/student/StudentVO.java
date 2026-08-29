package com.kh.khedu.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="계정정보 제외 학생정보 등록")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentVO {
	private int studentNo;
	private int accountNo;
	private Integer consultCustomerNo; // 상담고객번호
	private String studentSchool; //null가능
	private String studentGrade; //초1,초2...
	private String studentGender; //남/여
	private String studentEtc; // 비고
}
