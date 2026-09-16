package com.kh.khedu.service;

import com.kh.khedu.vo.dashboard.AcademyDashboardVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface AcademyDashboardService {
	AcademyDashboardVO getDashboard(TokenParseResponseVO parseVO);
}
