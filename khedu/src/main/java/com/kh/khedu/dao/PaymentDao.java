package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.student.StudentCourseVO;

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
    
    // [자동 청구 스케줄러용 메서드 3개]
    List<Integer> selectAllActiveStudents(); // 전체 재원생 번호 조회
    int checkDuplicateBilling(int studentNo, String currentMonth); // 중복 청구 검사
    List<StudentCourseVO> selectStudentCourses(int studentNo); // 학생의 수강 강좌 및 금액 조회
}
