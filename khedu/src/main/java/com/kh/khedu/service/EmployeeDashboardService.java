package com.kh.khedu.service;

import com.kh.khedu.vo.dashboard.EmployeeDashboardVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface EmployeeDashboardService {

	 EmployeeDashboardVO getDashboard(TokenParseResponseVO parseVO);
}
