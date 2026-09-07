package com.kh.khedu.vo.consult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "상담 고객 목록 검색 요청 데이터")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultCustomerListRequestVO {
	private String search;
}
