package com.kh.khedu.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kh.khedu.vo.payment.kakao.KakaoPayApproveResponseVO;
import com.kh.khedu.vo.payment.kakao.KakaoPayReadyResponseVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    // properties 파일에서 값 쏙 빼오기
    @Value("${custom.kakaopay.secret-key}")
    private String secretKey;
    
    @Value("${custom.kakaopay.cid}")
    private String cid;

    // 카카오페이 API 주소 (새로운 버전)
    private final String KAKAO_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";

    public KakaoPayReadyResponseVO ready(int accountNo, String itemName, int totalAmount, int paymentNo) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 헤더 설정 (새로운 Secret Key 방식)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. 바디(요청 파라미터) 설정
        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        // 테스트용 고유 아이디들 (나중에는 실제 주문번호/회원번호로 변경)
        params.put("partner_order_id", String.valueOf(paymentNo)); 
        params.put("partner_user_id", String.valueOf(accountNo));
        params.put("item_name", itemName);       // 상품명 (ex: 9월 수학 수강료)
        params.put("quantity", "1");             // 수량
        params.put("total_amount", String.valueOf(totalAmount)); // 총 금액
        params.put("tax_free_amount", "0");      // 비과세 금액

        // 🌟 결제 성공/취소/실패 시 돌아올 리액트 프론트엔드 주소! (나중에 프론트에 이 화면들을 만들어야 합니다)
        params.put("approval_url", "http://localhost:5173/parent/payment/success");
        params.put("cancel_url", "http://localhost:5173/parent/payment/cancel");
        params.put("fail_url", "http://localhost:5173/parent/payment/fail");

        // 3. 헤더와 바디를 합쳐서 HTTP 요청 객체 생성
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // 4. 카카오페이로 쏘고, 응답을 KakaoReadyResponseVO로 받기
        KakaoPayReadyResponseVO response = restTemplate.postForObject(
                KAKAO_READY_URL, 
                requestEntity, 
                KakaoPayReadyResponseVO.class
        );

        // (중요 팁!) 여기서 응답받은 response.getTid()는 나중에 승인할 때 써야 하므로, 
        // 세션이나 DB에 임시로 저장해두는 로직이 나중에 추가되어야 합니다!

        return response;
    }
    
    private final String KAKAO_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

    public KakaoPayApproveResponseVO approve(String tid, String pgToken, int accountNo, int paymentNo) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", tid);
        params.put("partner_order_id", String.valueOf(paymentNo)); // ready때 썼던 번호 그대로!
        params.put("partner_user_id", String.valueOf(accountNo)); 
        params.put("pg_token", pgToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(KAKAO_APPROVE_URL, requestEntity, KakaoPayApproveResponseVO.class);
    }
    
 // 카카오페이 결제 취소 URL
    private final String KAKAO_CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

    public boolean cancelPayment(String tid, int cancelAmount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid); // 가맹점 코드 (테스트용 TC0ONETIME)
        params.put("tid", tid); // 🌟 환불할 결제건의 고유 번호!
        params.put("cancel_amount", String.valueOf(cancelAmount)); // 환불할 금액 (2000만원)
        params.put("cancel_tax_free_amount", "0"); // 비과세 금액

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            // 카카오페이에 취소 요청 쏘기!
            restTemplate.postForObject(KAKAO_CANCEL_URL, requestEntity, String.class);
            return true; // 성공 시 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 실패 시 false 반환
        }
    }
}