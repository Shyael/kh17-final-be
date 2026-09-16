package com.kh.khedu.vo.payment.kakao;

import lombok.Data;

@Data
public class KakaoPayReadyResponseVO {
    private String tid; // 결제 고유 번호 (승인할 때 꼭 필요함!)
    private String next_redirect_pc_url; // PC 웹 결제창 주소
    private String next_redirect_mobile_url; // 모바일 웹 결제창 주소
    private String created_at;
}