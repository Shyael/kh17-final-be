package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.dto.PaymentHistoryDto;
import com.kh.khedu.vo.payment.CoursePaymentVO;
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
    public boolean updateDiscount(DiscountVO discountVO) {
        return sqlSession.update("mapper.discount.updateDiscount", discountVO) > 0;
    }
    
    @Override
    public void deleteDiscount(int discountNo) {
    	sqlSession.delete("mapper.discount.deleteDiscount", discountNo);
    }
    
    @Override
    public List<Integer> selectAllActiveStudents() {
        // 결과가 단순 숫자(int)들의 모임이므로 List<Integer>로 바로 받습니다.
        return sqlSession.selectList("mapper.payment.selectAllActiveStudents");
    }

    @Override
    public int checkDuplicateBilling(int studentNo, String currentMonth) {
        // 파라미터가 2개이므로 Map에 담아서 전달
        Map<String, Object> params = new HashMap<>();
        params.put("studentNo", studentNo);
        params.put("currentMonth", currentMonth);
        return sqlSession.selectOne("mapper.payment.checkDuplicateBilling", params);
    }

    @Override
    public List<CoursePaymentVO> selectStudentCourses(int studentNo) {
        return sqlSession.selectList("mapper.payment.selectStudentCourses", studentNo);
    }
    
    @Override
    public PaymentDto selectPaymentMaster(int paymentNo) {
        return sqlSession.selectOne("mapper.payment.selectPaymentMaster", paymentNo);
    }
    
    @Override
    public List<PaymentDetailDto> selectPaymentDetails(int paymentNo) {
        return sqlSession.selectList("mapper.payment.selectPaymentDetails", paymentNo);
    }
    
    @Override
    public List<PaymentDiscountDto> selectPaymentDiscounts(int paymentNo) {
        return sqlSession.selectList("mapper.payment.selectPaymentDiscounts", paymentNo);
    }
    
    @Override
    public List<PaymentHistoryDto> selectPaymentHistorys(int paymentNo) {
    	return sqlSession.selectList("mapper.payment.selectPaymentHistorys", paymentNo);
    }
    
    @Override
    public void insertPaymentHistory(PaymentHistoryDto paymentHistoryDto) {
        sqlSession.insert("mapper.payment.insertPaymentHistory", paymentHistoryDto);
    }
    
    @Override
    public int getTotalPaidAmount(int paymentNo){
        return sqlSession.selectOne("mapper.payment.getTotalPaidAmount", paymentNo);
    }
    
    @Override
    public boolean updatePaymentStatus(Map<String, Object> params) {
        return sqlSession.update("mapper.payment.updatePaymentStatus", params) > 0;
    }
}