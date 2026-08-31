package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="상담 고객 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultCustomerDto {
	private Integer customerNo;
	private Integer studentNo;
	private String studentName;
	private String studentPhone;
	private String studentEmail;
	private String studentAddress;
	private String studentSchool;
	private String studentGrade;
	private String studentGender;
	private String parentName;
	private String parentPhone;
	private String parentEmail;
	private String customerRelation;
	private String customerMemo;
	private Timestamp customerCtime;
	private Timestamp customerUtime;
}
