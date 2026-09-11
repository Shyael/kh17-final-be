package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "학생 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentDto {
	private int studentNo;
	private int accountNo;
	private Integer consultCustomerNo; // 상담고객번호
	private String studentSchool; //null가능
	private String studentGrade; //초1,초2...
	private String studentGender; //남/여
	private String studentEtc; // 비고
	private Timestamp studentCtime; //학생 등록일(승인 처리가 된 시점, account_status가 Y가 된 시점)
	private String studentAcademicStatus; //학생 상태 (대기/재원/휴원/퇴원/수료)
	private Timestamp studentUtime; //학생 정보 수정시간
}
