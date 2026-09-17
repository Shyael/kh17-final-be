package com.kh.khedu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.khedu.controller.SseController;
import com.kh.khedu.vo.payroll.request.DashboardPendingContractVO;
import com.kh.khedu.vo.payroll.response.DashboardPayrollDueVO;
import com.kh.khedu.vo.sse.SseAlarmVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final ObjectMapper objectMapper;
    private final AdminDashboardService adminDashboardService;
    
    @Override
    public void sendToGroup(
            String accountType,
            String roleName,
            SseAlarmVO alarm
    ) {
        try {
            String message =
                    objectMapper.writeValueAsString(alarm);

            SseController.sendToGroup(
                    accountType,
                    roleName,
                    message
            );

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "알림 변환 실패", e
            );
        }
    }

    @Override
    public void sendToUser(
            String accountType,
            Integer accountNo,
            SseAlarmVO alarm
    ) {
        try {
            String message =
                    objectMapper.writeValueAsString(alarm);

            SseController.sendToUser(
                    accountType,
                    accountNo,
                    message
            );

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "알림 변환 실패", e
            );
        }
    }

	@Override
	
	public void sendPayrollDueAlarm() {
		// 1. 기존 대시보드 업무 목록 재사용
        List<DashboardPayrollDueVO> list =
                adminDashboardService.getPayrollDueList();

        // 2. 지급일이 5일 이내이거나 미지급인 급여 확인
        boolean exists = list.stream()
                .anyMatch(payroll ->
                        "unpaid".equals(payroll.getPaymentStatus())
                        ||(payroll.getDDay() >= 0
                        && payroll.getDDay() <= 5)
                );

        if (!exists) return;

        // 3. 알림 조립
        SseAlarmVO alarm = SseAlarmVO.builder()
                .type("급여")
                .message("지급해야 할 급여가 존재합니다.")
                .targetUrl("/admin/payroll/")
                .build();

        // 4. 원장에게 전송
        sendToGroup("직원", "ADMIN", alarm);
        //5.데스크에게 전송
        sendToGroup("직원", "DESK", alarm);
		
	}

	@Override
	public void checkWaitingEmployee() {
		List<DashboardPendingContractVO> list =adminDashboardService.getPendingContractList();
		boolean exists = !list.isEmpty();
		if(!exists) return;
		
		 // 3. 알림 조립
        SseAlarmVO alarm = SseAlarmVO.builder()
                .type("계약")
                .message("대기 상태의 직원이 존재합니다.")
                .targetUrl("/admin/contract/list")
                .build();

        // 4. 원장에게 전송
        sendToGroup("직원", "ADMIN", alarm);
	}
}