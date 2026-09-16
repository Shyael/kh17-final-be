package com.kh.khedu.service;





import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AdminDashboardDao;
import com.kh.khedu.vo.payroll.request.DashboardPayrollDueQueryVO;
import com.kh.khedu.vo.payroll.response.DashboardPayrollDueVO;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class DeskDashboardServiceimpl implements DeskDashboardService {
	@Autowired
	private AdminDashboardDao adminDashboardDao;
	
	//급여 쪽은 제가 했어요

	public List<DashboardPayrollDueVO> getPayrollDueList() {

	    List<DashboardPayrollDueQueryVO> list =
	            adminDashboardDao.getPayrollDueList();

	    LocalDate today = LocalDate.now();

	   
	    return list.stream()
	            .map(query -> {

	                LocalDate payday = LocalDate.of(
	                        query.getPayrollYear(),
	                        query.getPayrollMonth(),
	                        query.getPayday()
	                );

	                long dDay = ChronoUnit.DAYS.between(
	                        today,
	                        payday
	                );

	                return DashboardPayrollDueVO.builder()
	                        .payrollNo(query.getPayrollNo())
	                        .employeeNo(query.getEmployeeNo())
	                        .employeeName(query.getEmployeeName())
	                        .payrollYear(query.getPayrollYear())
	                        .payrollMonth(query.getPayrollMonth())
	                        .payday(payday)
	                        .netPay(query.getNetPay())
	                        .paymentStatus(query.getPaymentStatus())
	                        .dDay(dDay)
	                        .build();
	            })
	            .toList();
	    
	   	}
	
	
}
