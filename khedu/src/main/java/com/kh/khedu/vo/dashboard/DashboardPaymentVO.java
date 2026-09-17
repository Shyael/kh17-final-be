package com.kh.khedu.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPaymentVO {
    private int paymentNo;
    private int studentNo;
    private String studentName;
    private String paymentMonth;   // 예: "2026-08"
    private int remainingAmount; // 미납액
}