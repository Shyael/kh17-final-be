package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.KakaoPayService;
import com.kh.khedu.service.PaymentService;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.payment.kakao.KakaoPayReadyResponseVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/academy/payment")
@RequiredArgsConstructor
public class AcademyPaymentRestController {

	@Autowired
	private KakaoPayService kakaoPayService;
	@Autowired
	private PaymentService paymentService;
	
	// 카카오페이 결제 준비 요청을 받는 API
    @PostMapping("/kakaopay/ready")
    public ResponseEntity<KakaoPayReadyResponseVO> readyPayment(
            @RequestParam int accountNo, 
            @RequestParam String itemName, 
            @RequestParam int totalAmount, 
            @RequestParam int paymentNo) {

        // 서비스 호출해서 카카오페이에 통신하고 URL 받아오기
        KakaoPayReadyResponseVO response = kakaoPayService.ready(accountNo, itemName, totalAmount, paymentNo);
        
        return ResponseEntity.ok(response);
    }
    
        

        // 특정 학생의 수납(청구) 목록 조회 API
        // GET /api/academy/payment/list?studentNo=5
        @GetMapping("/list")
        public ResponseEntity<List<PaymentListResponseVO>> getStudentPaymentList(@RequestParam int studentNo) {
            // 방금 만든 서비스 메서드 호출!
            List<PaymentListResponseVO> list = paymentService.getPaymentListByStudentNo(studentNo);
            return ResponseEntity.ok(list);
        }
        
        @PostMapping("/kakaopay/approve")
        public ResponseEntity<String> approvePayment(
                @RequestParam String pg_token, 
                @RequestParam String tid,
                @RequestParam int paymentNo, 
                @RequestParam int accountNo, 
                @RequestParam int amount) {
            
            // 1. 카카오페이 찐 승인 요청!
            kakaoPayService.approve(tid, pg_token, accountNo, paymentNo);
            
            // 2. 카카오 승인이 정상적으로 뚫리면, 우리 DB에 납부 완료 처리!
            paymentService.processPaymentSuccess(paymentNo, amount, tid);
            
            return ResponseEntity.ok("SUCCESS");
        }
}
