package com.kh.khedu.vo.consult;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "상담 예약 목록 조회 정보")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConsultReservationListItemVO {
	private int reservationNo;
	private String reservationName;
	private String reservationPhone;
	private String reservationType;
	private Timestamp reservationTime;
	private String reservationStatus;
	private String reservationStatusString;
	private String reservationComment;
	private Timestamp reservationCtime;
}
