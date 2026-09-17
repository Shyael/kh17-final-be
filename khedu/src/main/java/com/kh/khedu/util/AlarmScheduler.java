package com.kh.khedu.util;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.khedu.service.AlarmService;

@Component
public class AlarmScheduler {

    @Autowired
    private AlarmService alarmService;

    @Scheduled(
        cron = "0 0 9 * * *",
        zone = "Asia/Seoul"
    )
    public void checkPayroll() {

        alarmService.sendPayrollDueAlarm();

    }
    
    @Scheduled(
            cron = "0 0 9 * * *",
            zone = "Asia/Seoul"
        )
    public void checkWaitingEmployee() {
    	alarmService.checkWaitingEmployee();
    }
}