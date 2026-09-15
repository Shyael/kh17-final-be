package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.KakaoPayService;
import com.kh.khedu.service.PaymentService;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentComprehensiveVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.payment.PaymentRequestVO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "수납 관리")
@RestController
@RequestMapping("/api/employee/payment")
public class PaymentRestController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private KakaoPayService kakaoPayService;

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
    
    // 4. 할인 삭제
    @DeleteMapping("/discount/delete")
    public ResponseEntity<String> deleteDiscount(int discountNo){
    	paymentService.deleteDiscount(discountNo);
    	return ResponseEntity.ok("할인이 삭제되었습니다.");
    }
    
    // 특정 학생 결제 내역 조회 API
    @GetMapping("/student/{studentNo}")
    public List<PaymentListResponseVO> getStudentPayments(@PathVariable int studentNo) {
        return paymentService.getStudentPayments(studentNo);
    }
    
    // 수납 상세 내역 조회 API
    @GetMapping("/detail/{paymentNo}")
    public ResponseEntity<?> getPaymentDetail(@PathVariable int paymentNo) {
        try {
            PaymentComprehensiveVO response = paymentService.getPaymentDetail(paymentNo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
 // 수납(결제) 처리 API
    @PostMapping("/pay")
    public ResponseEntity<String> addPaymentHistory(
            @RequestParam int paymentNo, 
            @RequestParam int payAmount) {
        try {
            paymentService.addPaymentHistory(paymentNo, payAmount);
            return ResponseEntity.ok("수납 처리가 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("수납 처리 실패");
        }
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPaymentHistory(
            @RequestParam int paymentHistoryNo, 
            @RequestParam int amount,           
            @RequestParam int paymentNo,
            @RequestParam(required = false) String tid,
            @RequestParam(required = false) String cancelReason) { // 🌟 사유 추가

        // 1. 카카오페이 결제건 환불 (tid가 있을 때)
        if (tid != null && !tid.isEmpty() && !tid.equals("null")) {
            boolean isCancelled = kakaoPayService.cancelPayment(tid, amount);
            if (!isCancelled) {
                return ResponseEntity.status(500).body("카카오페이 환불에 실패했습니다.");
            }
        }
        
        // 2. 카카오페이 환불 성공 OR 수기 결제인 경우 -> DB 상태를 '취소'로 변경!
        // 사유가 안 넘어왔다면 기본 멘트 세팅
        String reason = (cancelReason != null && !cancelReason.isEmpty()) ? cancelReason : "관리자 수기 취소";
        paymentService.cancelHistoryAndUpdateStatus(paymentHistoryNo, paymentNo, reason);
        
        return ResponseEntity.ok("결제가 성공적으로 취소되었습니다.");
    }
}