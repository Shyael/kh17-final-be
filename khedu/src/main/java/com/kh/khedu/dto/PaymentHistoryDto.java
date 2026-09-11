package com.kh.khedu.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PaymentHistoryDto {
	private int paymentHistoryNo;
	private int paymentNo;
	private int paymentHistoryAmount;
	private Timestamp paymentHistoryAt;
}
