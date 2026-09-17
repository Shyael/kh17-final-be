package com.kh.khedu.service;

import com.kh.khedu.vo.sse.SseAlarmVO;

public interface AlarmService {
	void sendToGroup(
            String accountType,
            String roleName,
            SseAlarmVO alarm
    );

    void sendToUser(
            String accountType,
            Integer accountNo,
            SseAlarmVO alarm
    );
	 
    void sendPayrollDueAlarm();
	 
    void checkWaitingEmployee();
}
