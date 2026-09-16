package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.vo.payroll.request.DashboardContractExpiringVO;
import com.kh.khedu.vo.payroll.request.DashboardPendingContractVO;
import com.kh.khedu.vo.payroll.response.DashboardPayrollDueVO;

public interface AdminDashboardService {
	List<DashboardPayrollDueVO> getPayrollDueList();
	
	 List<DashboardPendingContractVO> getPendingContractList();

	    List<DashboardContractExpiringVO> getContractExpiringList();
}
