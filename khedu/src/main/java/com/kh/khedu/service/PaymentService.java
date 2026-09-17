package com.kh.khedu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.controller.SseController;
import com.kh.khedu.dao.PaymentDao;
import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.dto.PaymentDetailDto;
import com.kh.khedu.dto.PaymentDiscountDto;
import com.kh.khedu.dto.PaymentDto;
import com.kh.khedu.dto.PaymentHistoryDto;
import com.kh.khedu.vo.payment.CoursePaymentVO;
import com.kh.khedu.vo.payment.DiscountVO;
import com.kh.khedu.vo.payment.PaymentComprehensiveVO;
import com.kh.khedu.vo.payment.PaymentDetailVO;
import com.kh.khedu.vo.payment.PaymentDiscountVO;
import com.kh.khedu.vo.payment.PaymentListResponseVO;
import com.kh.khedu.vo.payment.PaymentRequestVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.sse.SseAlarmVO;

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
    
    // 할인 삭제
    public void deleteDiscount(int discountNo) {
    	paymentDao.deleteDiscount(discountNo);
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
            
            // [방어 로직] 이 학생의 '이번 달' 청구서가 이미 존재하는지 카운트 확인
            int isAlreadyBilled = paymentDao.checkDuplicateBilling(studentNo, currentMonth);
            if (isAlreadyBilled > 0) {
                continue; // 이미 이번 달 청구서가 있으면 다음 학생으로 패스! (중복 발행 차단)
            }
            
            // 2. 학생이 듣고 있는 강좌 목록을 가져와서 '원금 총액' 계산
            List<CoursePaymentVO> courses = paymentDao.selectStudentCourses(studentNo);
            if (courses.isEmpty()) {
                continue; // 듣는 강좌가 없으면 청구서 발행 안 함
            }
            
            int totalFee = 0;
            for (CoursePaymentVO course : courses) {
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
            for (CoursePaymentVO course : courses) {
                PaymentDetailDto detail = new PaymentDetailDto();
                detail.setPaymentNo(paymentNo);
                detail.setCourseNo(course.getCourseNo());
                detail.setCourseFee(course.getCourseFee());
                paymentDao.insertPaymentDetail(detail);
            }
            
            // (선택) 6. DB에 어떤 할인이 들어갔는지 기록(payment_discount) INSERT
            // 이 테이블이 있다면 반복문 돌려서 기록해 주면 나중에 영수증 볼 때 아주 좋습니다.
            if (discounts != null && !discounts.isEmpty()) {
                for (StudentDiscountVO discount : discounts) {
                    PaymentDiscountDto discountDto = new PaymentDiscountDto();
                    discountDto.setPaymentNo(paymentNo);
                    discountDto.setDiscountName(discount.getDiscountName()); // 할인 이름 (예: 형제할인)
                    
                    // 영수증에 보여줄 "실제로 깎인 금액" 계산
                    int actualDiscountAmount = 0;
                    if ("비율".equals(discount.getDiscountType())) {
                        actualDiscountAmount = (totalFee * discount.getDiscountValue() / 100);
                    } else {
                        actualDiscountAmount = discount.getDiscountValue();
                    }
                    
                    // DB에는 %가 아닌 '실제 차감된 돈'을 기록해둡니다.
                    discountDto.setDiscountValue(actualDiscountAmount); 
                    
                    paymentDao.insertPaymentDiscount(discountDto);
                    
                    }
                }
            // 1. 학생 이름 조회
            String studentName = paymentDao.selectStudentName(studentNo);
            
            // 2. 학부모에게 보낼 알람 객체 생성 (paymentMonth는 파라미터로 받은 currentMonth 사용)
            SseAlarmVO billingAlarm = SseAlarmVO.builder()
            		.type("PAYMENT")
            		.message("[" + studentName + "] 학생의 " + currentMonth + "월 학원비가 청구되었습니다.")
            		.targetNo(paymentNo)
            		.targetUrl("/parent/payment/list") // 학부모용 수납 목록 페이지 이동
            		.build();
            
            // 3. 학부모 개인에게 알람 발송
            Integer parentAccountNo = paymentDao.selectParentAccountNoByStudentNo(studentNo); 
            
            if(parentAccountNo != null) {
            	SseController.sendToUser("학부모", parentAccountNo, billingAlarm);
            }
        }
    }
    
    // 수납 상세 정보 종합 세트 조립 로직
    public PaymentComprehensiveVO getPaymentDetail(int paymentNo) {
        
        // 1. 마스터 정보 가져오기 (없으면 에러 처리)
        PaymentDto master = paymentDao.selectPaymentMaster(paymentNo);
        if(master == null) {
            throw new RuntimeException("존재하지 않는 수납 번호입니다.");
        }
        
        // 2. 상세 및 할인 내역 및 수납 이력 리스트 가져오기
        List<PaymentDetailDto> details = paymentDao.selectPaymentDetails(paymentNo);
        List<PaymentDiscountDto> discounts = paymentDao.selectPaymentDiscounts(paymentNo);
        List<PaymentHistoryDto> historys = paymentDao.selectPaymentHistorys(paymentNo);

        // 3. 하나의 VO로 조립해서 반환
        return PaymentComprehensiveVO.builder()
                .payment(master)
                .details(details)
                .discounts(discounts)
                .historys(historys)
                .build();
    }
    
    @Transactional
    public void addPaymentHistory(int paymentNo, int payAmount) {
        // 1. 납부 이력 DB에 INSERT
        PaymentHistoryDto historyDto = new PaymentHistoryDto();
        historyDto.setPaymentNo(paymentNo);
        historyDto.setPaymentHistoryAmount(payAmount);
        paymentDao.insertPaymentHistory(historyDto);

        // 2. 지금까지 낸 총액과 원래 내야 할 원금(청구액) 조회
        int totalPaid = paymentDao.getTotalPaidAmount(paymentNo);
        PaymentDto master = paymentDao.selectPaymentMaster(paymentNo);

        // 3. 상태 결정
        String newStatus = "미납";
        if (totalPaid >= master.getPaymentAmount()) {
            newStatus = "완납";
        } else if (totalPaid > 0) {
            newStatus = "부분납";
        }

        // 4. 영수증 마스터 상태 업데이트 
        Map<String, Object> params = new HashMap<>();
        params.put("paymentNo", paymentNo);
        params.put("paymentStatus", newStatus);
        paymentDao.updatePaymentStatus(params); 
        
        // 5. [알림 발송] 직원(원장, 데스크)에게 수기 수납 완료 알림 보내기!
        String studentName = paymentDao.selectStudentName(master.getStudentNo());

        SseAlarmVO paidAlarm = SseAlarmVO.builder()
                .type("PAYMENT")
                .message("[" + studentName + "] 학생의 학원비(" + payAmount + "원) 현장 수납이 완료되었습니다.")
                .targetNo(paymentNo)
                .targetUrl("/employee/payment/detail/" + paymentNo) 
                .build();

        SseController.sendToGroup("직원", "ADMIN", paidAlarm);
        SseController.sendToGroup("직원", "DESK", paidAlarm);
    }
    
    public List<PaymentListResponseVO> getPaymentListByStudentNo(int studentNo) {
        return paymentDao.selectPaymentListByStudentNo(studentNo);
    }
    
    @Transactional // 도중에 에러나면 롤백되도록!
    public void processPaymentSuccess(int paymentNo, int paidAmount, String tid) {
        // 1. payment_history (납부 내역) 테이블에 인서트!
        PaymentHistoryDto history = new PaymentHistoryDto();
        history.setPaymentNo(paymentNo);
        history.setPaymentHistoryAmount(paidAmount);
        history.setPaymentHistoryTid(tid);
        paymentDao.insertPaymentHistory(history);

        // 2. 이 영수증의 총 납부액 조회 (방금 낸 돈 포함해서 얼마 냈나?)
        int totalPaid = paymentDao.getTotalPaidAmount(paymentNo);
        PaymentDto master = paymentDao.selectPaymentMaster(paymentNo);

        // 3. 낸 돈이 청구액보다 크거나 같으면 '완납', 아니면 '부분납' 처리!
        String status = (totalPaid >= master.getPaymentAmount()) ? "완납" : "부분납";
        
        // 4. Map으로 파라미터 예쁘게 포장해서 DAO로 던지기!
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("paymentNo", paymentNo);
        updateParams.put("paymentStatus", status);
        paymentDao.updatePaymentStatus(updateParams);
        
        // 5. [알림 발송] 직원(원장, 데스크)에게 수납 완료 알림 보내기!
        // 마스터 정보에서 학생 번호를 꺼내 이름을 조회해옵니다.
        String studentName = paymentDao.selectStudentName(master.getStudentNo());

        SseAlarmVO paidAlarm = SseAlarmVO.builder()
                .type("PAYMENT")
                .message("[" + studentName + "] 학생의 학원비(" + paidAmount + "원) 카카오페이 수납이 완료되었습니다.")
                .targetNo(paymentNo)
                .targetUrl("/employee/payment/detail/" + paymentNo) 
                .build();

        SseController.sendToGroup("직원", "ADMIN", paidAlarm);
        SseController.sendToGroup("직원", "DESK", paidAlarm);
    }
    
    @Transactional
    public void cancelHistoryAndUpdateStatus(int paymentHistoryNo, int paymentNo, String reason) {
        
        // 1. 해당 납부 이력을 '결제취소' 상태로 업데이트 (Soft Delete)
        Map<String, Object> cancelParams = new HashMap<>();
        cancelParams.put("paymentHistoryNo", paymentHistoryNo);
        cancelParams.put("cancelReason", reason);
        paymentDao.updatePaymentHistoryCancel(cancelParams); // 새로 만든 쿼리 실행
        
        // 2. 남은 유효한 납부액 다시 계산 (이제 취소된 건 알아서 빠짐!)
        int currentTotalPaid = paymentDao.getTotalPaidAmount(paymentNo);
        PaymentDto master = paymentDao.selectPaymentMaster(paymentNo);
        
        // 3. 상태 롤백 로직
        String newStatus;
        if (currentTotalPaid == 0) {
            newStatus = "미납";
        } else if (currentTotalPaid < master.getPaymentAmount()) {
            newStatus = "부분납";
        } else {
            newStatus = "완납";
        }
        
        // 4. 마스터 영수증 상태 업데이트
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("paymentNo", paymentNo);
        updateParams.put("paymentStatus", newStatus);
        paymentDao.updatePaymentStatus(updateParams);
    }
}