package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.dto.PaymentHistoryDto;
import com.kh.khedu.vo.payment.CoursePaymentVO;
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
    boolean updateDiscount(DiscountVO discountVO);
    void deleteDiscount(int discountNo);
    
    // [자동 청구 스케줄러용 메서드 3개]
    List<Integer> selectAllActiveStudents(); // 전체 재원생 번호 조회
    int checkDuplicateBilling(int studentNo, String currentMonth); // 중복 청구 검사
    List<CoursePaymentVO> selectStudentCourses(int studentNo); // 학생의 수강 강좌 및 금액 조회
    
    //수납 상세목록 조회
    // 1. 수납 마스터 조회 (단건)
    PaymentDto selectPaymentMaster(int paymentNo);
    // 2. 수납 상세 목록 조회 (다건)
    List<PaymentDetailDto> selectPaymentDetails(int paymentNo);
    // 3. 수납 할인 목록 조회 (다건)
    List<PaymentDiscountDto> selectPaymentDiscounts(int paymentNo);
    // 4. 수납 이력 목록 조회 (다건)
    List<PaymentHistoryDto> selectPaymentHistorys(int paymentNo);
    
    // 관리자가 수납하기
 // 1. 납부 이력 저장
    void insertPaymentHistory(PaymentHistoryDto paymentHistoryDto);
    // 2. 해당 영수증의 총 납부액 계산 (결과값은 단일 숫자 int)
    int getTotalPaidAmount(int paymentNo);
    // 3. 영수증(마스터) 상태 업데이트 (상태값과 번호를 Map에 담아 받음)
    boolean updatePaymentStatus(Map<String, Object> params);
}
