package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;

public interface PaymentDao {

	//입력
	int sequence();
	void insertPayment(PaymentDto paymentDto);
	void insertPaymentDetail(PaymentDetailDto paymentDetailDto);
	void insertPaymentDiscount(PaymentDiscountDto paymentDiscountDto);
	
	//조회
	List<PaymentListResponseVO> selectPaymentList(Map<String, Object> params);
	
	//할인 정보 관리
	List<DiscountVO> selectDiscountList();
    void insertDiscount(DiscountVO discountVO);
    void updateDiscount(DiscountVO discountVO);
}
