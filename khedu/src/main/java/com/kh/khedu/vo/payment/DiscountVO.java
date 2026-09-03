package com.kh.khedu.vo.payment;
import lombok.Data;

@Data
public class DiscountVO {
    private int discountNo;
    private String discountName;
    private String discountType;   // "비율" or "금액"
    private int discountValue;
    private String discountStatus; // "Y" (활성화) or "N" (비활성화)
}