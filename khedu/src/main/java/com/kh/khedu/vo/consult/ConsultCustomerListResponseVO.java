package com.kh.khedu.vo.consult;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "상담 고객 목록 조회 결과")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultCustomerListResponseVO {
	private List<ConsultCustomerListItemVO> items;
}
