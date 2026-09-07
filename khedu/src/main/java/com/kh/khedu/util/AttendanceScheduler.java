package com.kh.khedu.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.khedu.service.attendance.EmployeeAttendanceService;
@Component
public class AttendanceScheduler {
	 @Autowired
	    private EmployeeAttendanceService employeeAttendanceService;

	 @Scheduled(
		        cron = "0 * * * * *",
		        zone = "Asia/Seoul"
		    )
		    public void autoAbsent() {

		        employeeAttendanceService.autoAbsent();
		    }

}
