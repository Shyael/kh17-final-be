package com.kh.khedu.vo.payment;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class PaymentListResponseVO {
    // 1. 식별자
    private int paymentNo;        // 수납번호 (프론트에서 PAY-1031 처럼 조립해서 씀)
    
    // 2. 조인(JOIN)으로 가져오는 학생 정보
    private String studentName;   // 학생 이름 (account 테이블)
    
    // 3. 마스터 결제 정보 (payment 테이블)
    private String paymentMonth;  // 수납 월 (예: "2026-08")
    private String paymentStatus; // 납부 상태 ("완납", "미납", "부분납")
    private int totalAmount;      // 총 학원비 (payment_amount)
    
    // 4. 서브 쿼리 연산으로 만들어낸 통계 정보
    private int paidAmount;       // 지금까지 낸 납부 금액 총합
    private int remainingAmount;  // 남은 미납 금액 (총액 - 납부 금액)
    private Timestamp lastPaidDate; // 가장 최근 납부일
}