package com.kh.khedu.vo.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class CoursePaymentVO {
	private int studentNo;
	private int courseNo;
	private int courseFee;
}
