package com.kh.khedu.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AdminDashboardDao;
import com.kh.khedu.vo.payroll.request.DashboardContractExpiringVO;
import com.kh.khedu.vo.payroll.request.DashboardPayrollDueQueryVO;
import com.kh.khedu.vo.payroll.request.DashboardPendingContractVO;
import com.kh.khedu.vo.payroll.response.DashboardPayrollDueVO;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService{
	
	@Autowired
	private AdminDashboardDao adminDashboardDao;
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
	
	
	 @Override
	    public List<DashboardPendingContractVO> getPendingContractList() {
	        return adminDashboardDao.findPendingContractList();
	    }

	 
	    	
	 
	 @Override
	    	
	 public List<DashboardContractExpiringVO> getContractExpiringList() {

	    	    List<DashboardContractExpiringVO> list =
	    	            adminDashboardDao.findContractExpiringList();

	    	    LocalDate today = LocalDate.now();

	    	    list.forEach(vo -> {
	    	        long dDay = ChronoUnit.DAYS.between(
	    	                today,
	    	                vo.getContractEnd().toLocalDateTime().toLocalDate()
	    	        );

	    	        vo.setDDay(dDay);
	    	    });

	    	    return list;
	    	}
	    
	
}
