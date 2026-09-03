package com.kh.khedu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PaymentDetailDto {
	private int paymentDetailNo;
	private int paymentNo;
	private int courseNo;
	private int courseFee;
}
