package com.kh.khedu.vo.payment;
import lombok.Data;

@Data
public class StudentDiscountVO {
    private int studentDiscountNo;
    private int studentNo;
    private int discountNo;
    
    // 화면(StudentDetail)에 뿌려주기 위해 조인해서 가져올 추가 정보들
    private String discountName;
    private String discountType;
    private int discountValue;
}