package com.kh.khedu.vo.consult;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "상담 내역 저장 결과")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultUpdateResponseVO {
	private boolean result;
	private String errMsg;
	private Integer consultNo;
}
