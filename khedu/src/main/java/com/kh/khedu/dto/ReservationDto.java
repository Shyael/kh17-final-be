package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="상담 예약 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservationDto {
	private int reservationNo;
	private String reservationName;
	private String reservationPhone;
	private String reservationType;
	private Timestamp reservationTime;
	private String reservationStatus;
	private String reservationComment;
	private Timestamp reservationCtime;
	
	public String getReservationStatusString() {
		switch(reservationStatus) {
			case "0": return "상담 대기";
			case "1": return "예약 확정";
			case "2": return "상담 완료";
			case "9": return "예약 취소";
			default: return "알수없음";
		}
	}
}
