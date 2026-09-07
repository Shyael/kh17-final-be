package com.kh.khedu.vo.consult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "상담 내역 조회 정보")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultListItemVO {
	private Integer consultNo;
	private int customerNo;
	private int consultEmployeeNo;
	private String accountName;
	private String employeeType;
	private String consultTitle;
	private String consultContent;
	private String consultCtime;
}
