package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.vo.payroll.request.DashboardContractExpiringVO;
import com.kh.khedu.vo.payroll.request.DashboardPayrollDueQueryVO;
import com.kh.khedu.vo.payroll.request.DashboardPendingContractVO;

public interface AdminDashboardDao {
	List<DashboardPendingContractVO> findPendingContractList();

	List<DashboardContractExpiringVO> findContractExpiringList();

	List<DashboardPayrollDueQueryVO> getPayrollDueList();
}
