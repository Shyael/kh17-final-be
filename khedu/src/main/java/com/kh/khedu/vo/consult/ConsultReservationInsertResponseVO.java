package com.kh.khedu.vo.consult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "상담 예약 신청 응답 객체")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultReservationInsertResponseVO {
	private boolean result;
	private String errMsg;
}
