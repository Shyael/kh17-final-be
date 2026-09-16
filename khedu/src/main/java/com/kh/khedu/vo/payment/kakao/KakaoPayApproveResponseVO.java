package com.kh.khedu.vo.payment.kakao;
import lombok.Data;
@Data
public class KakaoPayApproveResponseVO {
    private String aid; // 요청 고유 번호
    private String tid; // 결제 고유 번호
    private String created_at;
    private String approved_at;
}