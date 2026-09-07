package com.kh.khedu.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.kh.khedu.service.PaymentService;

@Component
public class PaymentScheduler {

    @Autowired
    private PaymentService paymentService;

    // 실무 배포용: 매월 1일 새벽 0시 0분 0초에 실행
    // @Scheduled(cron = "0 0 0 1 * ?") 
    
    // 테스트용: 우선 버튼 누르듯 테스트하기 위해 1분마다 실행되게 주석 해제해서 써보세요!
//    @Scheduled(cron = "0 * * * * ?") 
    public void generateMonthlyPayment() {
        // 이번 달 문자열 생성 (예: "2026-09")
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        System.out.println("============== [자동 청구 스케줄러 작동 시작] : " + currentMonth + " ==============");
        paymentService.processMonthlyBilling(currentMonth);
        System.out.println("============== [자동 청구 스케줄러 작동 완료] ==============");
    }
}