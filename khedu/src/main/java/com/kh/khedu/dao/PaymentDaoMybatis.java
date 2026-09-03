package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;

@Repository
public class PaymentDaoMybatis implements PaymentDao {

    @Autowired
    private SqlSession sqlSession;

    // 1. 번호 발급 심부름
    @Override
    public int sequence() {
        return sqlSession.selectOne("mapper.payment.sequence");
    }

    // 2. 마스터 결제 내역 저장 심부름
    @Override
    public void insertPayment(PaymentDto paymentDto) {
        sqlSession.insert("mapper.payment.insertPayment", paymentDto);
    }

    // 3. 수강 과목 상세 저장 심부름
    @Override
    public void insertPaymentDetail(PaymentDetailDto paymentDetailDto) {
        sqlSession.insert("mapper.payment.insertPaymentDetail", paymentDetailDto);
    }

    // 4. 할인 상세 저장 심부름
    @Override
    public void insertPaymentDiscount(PaymentDiscountDto paymentDiscountDto) {
        sqlSession.insert("mapper.payment.insertPaymentDiscount", paymentDiscountDto);
    }
    
    //조회
    @Override
    public List<PaymentListResponseVO> selectPaymentList(Map<String, Object> params) {
        // mapper의 id인 "selectPaymentList"를 호출하고 검색 조건(params)을 던져줍니다.
        return sqlSession.selectList("mapper.payment.selectPaymentList", params);
    }
    
    @Override
    public List<DiscountVO> selectDiscountList() {
        return sqlSession.selectList("mapper.discount.selectDiscountList");
    }

    @Override
    public void insertDiscount(DiscountVO discountVO) {
        sqlSession.insert("mapper.discount.insertDiscount", discountVO);
    }

    @Override
    public void updateDiscount(DiscountVO discountVO) {
        sqlSession.update("mapper.discount.updateDiscount", discountVO);
    }
}