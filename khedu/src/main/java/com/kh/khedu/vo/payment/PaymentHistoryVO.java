package com.kh.khedu.vo.payment;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class PaymentHistoryVO {
	private int paymentHisoryAmount;
	private Timestamp paymentHistoryAt;
}
