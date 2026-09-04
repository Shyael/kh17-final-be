package com.kh.khedu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.PaymentDao;
import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentComprehensiveVO;
import com.kh.khedu.vo.payment.PaymentDetailVO;
import com.kh.khedu.vo.payment.PaymentDiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.payment.PaymentRequestVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.StudentCourseVO;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private StudentDao studentDao;

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
    
    @Transactional
    public void processMonthlyBilling(String currentMonth) {
        
        // 1. 현재 학원에 다니고 있는 전체 학생 번호 목록을 가져옵니다.
        List<Integer> targetStudents = paymentDao.selectAllActiveStudents();
        
        for (Integer studentNo : targetStudents) {
            
            // 🛡️ [방어 로직] 이 학생의 '이번 달' 청구서가 이미 존재하는지 카운트 확인
            int isAlreadyBilled = paymentDao.checkDuplicateBilling(studentNo, currentMonth);
            if (isAlreadyBilled > 0) {
                continue; // 이미 이번 달 청구서가 있으면 다음 학생으로 패스! (중복 발행 차단)
            }
            
            // 2. 학생이 듣고 있는 강좌 목록을 가져와서 '원금 총액' 계산
            // (만들어두신 복사 테이블을 조회하는 쿼리 결과를 받는 VO를 StudentCourseVO라 가정)
            List<StudentCourseVO> courses = paymentDao.selectStudentCourses(studentNo);
            if (courses.isEmpty()) {
                continue; // 듣는 강좌가 없으면 청구서 발행 안 함
            }
            
            int totalFee = 0;
            for (StudentCourseVO course : courses) {
                totalFee += course.getCourseFee(); // 미리 복사해 둔 수강료 합산
            }
            
            // 3. 학생이 받고 있는 할인 목록 가져와서 '할인 총액' 계산
            List<StudentDiscountVO> discounts = studentDao.selectStudentDiscounts(studentNo);
            int totalDiscount = 0;
            
            for (StudentDiscountVO discount : discounts) {
                if ("비율".equals(discount.getDiscountType())) {
                    // 비율 할인이면 원금 기준 퍼센트 계산 (예: 250000 * 10 / 100 = 25000)
                    totalDiscount += (totalFee * discount.getDiscountValue() / 100);
                } else {
                    // 금액 할인이면 그대로 뺌
                    totalDiscount += discount.getDiscountValue();
                }
            }
            
            // 최종 청구 금액 (할인이 원금보다 커서 마이너스가 되는 것을 방지: Math.max 사용)
            int finalAmount = Math.max(0, totalFee - totalDiscount);
            
            // 4. DB에 영수증 마스터(payment) INSERT
            PaymentDto payment = new PaymentDto();
            int paymentNo = paymentDao.sequence(); // 시퀀스 발급
            payment.setPaymentNo(paymentNo);
            payment.setStudentNo(studentNo);
            payment.setPaymentMonth(currentMonth);
            payment.setPaymentAmount(finalAmount);
            payment.setPaymentStatus("미납"); // 최초 발행이므로 무조건 미납
            
            paymentDao.insertPayment(payment);
            
            // 5. DB에 수강 내역 디테일(payment_detail) INSERT
            for (StudentCourseVO course : courses) {
                PaymentDetailDto detail = new PaymentDetailDto();
                detail.setPaymentNo(paymentNo);
                detail.setCourseNo(course.getCourseNo());
                detail.setCourseFee(course.getCourseFee());
                paymentDao.insertPaymentDetail(detail);
            }
            
            // (선택) 6. DB에 어떤 할인이 들어갔는지 기록(payment_discount) INSERT
            // 이 테이블이 있다면 반복문 돌려서 기록해 주면 나중에 영수증 볼 때 아주 좋습니다.
        }
    }
    
    // 수납 상세 정보 종합 세트 조립 로직
    public PaymentComprehensiveVO getPaymentDetail(int paymentNo) {
        
        // 1. 마스터 정보 가져오기 (없으면 에러 처리)
        PaymentDto master = paymentDao.selectPaymentMaster(paymentNo);
        if(master == null) {
            throw new RuntimeException("존재하지 않는 수납 번호입니다.");
        }
        
        // 2. 상세 및 할인 내역 리스트 가져오기
        List<PaymentDetailDto> details = paymentDao.selectPaymentDetails(paymentNo);
        List<PaymentDiscountDto> discounts = paymentDao.selectPaymentDiscounts(paymentNo);

        // 3. 하나의 VO로 조립해서 반환
        return PaymentComprehensiveVO.builder()
                .payment(master)
                .details(details)
                .discounts(discounts)
                .build();
    }
}