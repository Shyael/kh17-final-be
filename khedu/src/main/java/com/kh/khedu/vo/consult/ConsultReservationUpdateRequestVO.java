package com.kh.khedu.vo.consult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "상담 예약 수정 요청 데이터")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultReservationUpdateRequestVO {
	private String reservationStatus;
	private String reservationComment;
}
