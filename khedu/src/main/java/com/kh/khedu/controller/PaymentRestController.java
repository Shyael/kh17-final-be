package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.PaymentService;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.payment.PaymentRequestVO;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<String> process(@RequestBody PaymentRequestVO request) {
        
        // 서비스 호출 (영수증 + 상세과목 + 할인내역 일괄 저장)
        paymentService.processPayment(request);
        
        return ResponseEntity.ok("결제 처리가 완료되었습니다.");
    }
    
    @GetMapping(value = "/list", produces = "application/json")
    public List<PaymentListResponseVO> list(
            @RequestParam(required = false) String searchMonth,
            @RequestParam(required = false, defaultValue = "전체") String searchStatus,
            @RequestParam(required = false) String searchName) {
        
        // DAO와 Service를 거쳐 위의 매퍼 쿼리를 실행하도록 연결해 주세요!
        return paymentService.getPaymentList(searchMonth, searchStatus, searchName);
    }
    
    // 1. 할인 목록 불러오기
    @GetMapping("/discount/list")
    public List<DiscountVO> getDiscountList() {
        return paymentService.getDiscountList();
    }

    // 2. 할인 등록하기
    @PostMapping("/discount/add")
    public ResponseEntity<String> addDiscount(@RequestBody DiscountVO discountVO) {
        paymentService.addDiscount(discountVO);
        return ResponseEntity.ok("새로운 할인이 등록되었습니다.");
    }

    // 3. 할인 수정/활성화/비활성화 처리
    @PutMapping("/discount/edit")
    public ResponseEntity<String> editDiscount(@RequestBody DiscountVO discountVO) {
        paymentService.editDiscount(discountVO);
        return ResponseEntity.ok("할인 정보가 수정되었습니다.");
    }
    
    // 특정 학생 결제 내역 조회 API
    @GetMapping("/student/{studentNo}")
    public List<PaymentListResponseVO> getStudentPayments(@PathVariable int studentNo) {
        return paymentService.getStudentPayments(studentNo);
    }
}