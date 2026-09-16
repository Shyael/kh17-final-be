package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.vo.payroll.response.DashboardPayrollDueVO;

public interface DeskDashboardService {
	List<DashboardPayrollDueVO> getPayrollDueList();
}
