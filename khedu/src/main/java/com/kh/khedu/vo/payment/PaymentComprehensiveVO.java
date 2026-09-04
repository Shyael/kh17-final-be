package com.kh.khedu.vo.payment;

import java.util.List;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentComprehensiveVO {
    // 1. 수납 마스터 정보 (청구 월, 총액, 미납액 등)
    private PaymentDto payment;
    
    // 2. 수납 상세 내역 (어떤 과목/교재가 청구되었는지)
    private List<PaymentDetailDto> details;
    
    // 3. 적용된 할인 내역 (어떤 할인이 마이너스 되었는지)
    private List<PaymentDiscountDto> discounts;
}