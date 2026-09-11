package com.kh.khedu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PaymentDiscountDto {
	private int paymentDiscountNo;
	private int paymentNo;
	private String discountName;
	private int discountValue;
}
