package com.kh.khedu.vo.consult;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "상담 예약 신청 요청 데이터")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultReservationInsertRequestVO {
	@Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer reservationNo;
	private String reservationName;
	private String reservationPhone;
	private String reservationType;
	private LocalDateTime reservationTime;
}
