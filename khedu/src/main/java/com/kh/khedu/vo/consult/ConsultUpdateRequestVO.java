package com.kh.khedu.vo.consult;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "상담 정보 저장 요청 데이터")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultUpdateRequestVO {
	private Integer consultNo;
	private int customerNo;
	private int consultEmployeeNo;
	private String consultTitle;
	private String consultContent;
}
