package com.kh.khedu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.PaymentDao;
import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentDetailVO;
import com.kh.khedu.vo.payment.PaymentDiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.payment.PaymentRequestVO;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Transactional
    public void processPayment(PaymentRequestVO request) {
        
        // 1. 영수증 번호 발급
        int paymentNo = paymentDao.sequence();

        //VO를 DTO로 변환
        PaymentDto paymentDto = new PaymentDto();
        
        // request(VO)에 있는 이름이 같은 데이터들을 paymentDto로 한방에 싹 복사합니다!
        BeanUtils.copyProperties(request, paymentDto); 
        
        // 발급받은 번호는 VO에 없었으니 따로 세팅해 줍니다.
        paymentDto.setPaymentNo(paymentNo); 

        // 2. DAO에는 깔끔하게 DTO만 던져줍니다! (에러 해결)
        paymentDao.insertPayment(paymentDto);


        // 디테일과 할인 내역도 똑같이 변환
        if (request.getDetails() != null) {
            for (PaymentDetailVO detailVO : request.getDetails()) {
                PaymentDetailDto detailDto = new PaymentDetailDto();
                BeanUtils.copyProperties(detailVO, detailDto); // 데이터 복사
                detailDto.setPaymentNo(paymentNo); // 영수증 번호 꼬리표
                
                paymentDao.insertPaymentDetail(detailDto); // DTO만 전달
            }
        }

        if (request.getDiscounts() != null) {
            for (PaymentDiscountVO discountVO : request.getDiscounts()) {
                PaymentDiscountDto discountDto = new PaymentDiscountDto();
                BeanUtils.copyProperties(discountVO, discountDto); // 데이터 복사
                discountDto.setPaymentNo(paymentNo); // 영수증 번호 꼬리표
                
                paymentDao.insertPaymentDiscount(discountDto); // DTO만 전달
            }
        }
    }
    
    public List<PaymentListResponseVO> getPaymentList(String searchMonth, String searchStatus, String searchName) {
        
        // 1. MyBatis 매퍼로 보낼 파라미터들을 Map에 담아 포장합니다.
        Map<String, Object> params = new HashMap<>();
        params.put("searchMonth", searchMonth);
        params.put("searchStatus", searchStatus);
        params.put("searchName", searchName);

        // 2. 포장한 Map을 통째로 DAO에게 넘겨줍니다!
        return paymentDao.selectPaymentList(params);
    }
    
    // 할인 목록 불러오기
    public List<DiscountVO> getDiscountList() {
        return paymentDao.selectDiscountList();
    }

    // 할인 등록하기
    public void addDiscount(DiscountVO discountVO) {
        paymentDao.insertDiscount(discountVO);
    }

    // 할인 수정/상태변경
    public void editDiscount(DiscountVO discountVO) {
        paymentDao.updateDiscount(discountVO);
    }
    
    // 특정 학생의 결제 내역만 가져오기
    public List<PaymentListResponseVO> getStudentPayments(int studentNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentNo", studentNo); // 맵에 학생 번호만 담아서 던집니다.
        
        return paymentDao.selectPaymentList(params);
    }
}