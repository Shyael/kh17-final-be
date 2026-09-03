package com.kh.khedu.vo.payment;

import java.util.List;
import lombok.Data;

@Data
public class PaymentRequestVO {
    //종합 정보(payment)
    private int studentNo;
    private String paymentMonth; //예:"2026-09"
    private int paymentAmount;   //최종 결제 금액
    private String paymentStatus;//"완납", "부분납", "미납"

    //디테일 정보들(List형태)
    private List<PaymentDetailVO> details; 
    private List<PaymentDiscountVO> discounts;
}