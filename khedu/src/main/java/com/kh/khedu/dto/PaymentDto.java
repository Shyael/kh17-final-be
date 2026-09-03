package com.kh.khedu.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PaymentDto {
	private int paymentNo;
	private int studentNo;
	private String paymentMonth;
	private int paymentAmount;
	private String paymentStatus;
	private Timestamp paymentCtime;
}
