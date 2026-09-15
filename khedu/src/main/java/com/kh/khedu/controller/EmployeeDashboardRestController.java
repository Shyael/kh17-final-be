package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.EmployeeDashboardService;
import com.kh.khedu.vo.dashboard.EmployeeDashboardVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name =  "직원 대시보드")
@RestController
@RequestMapping("/api/employee/dashboard")
public class EmployeeDashboardRestController {

	@Autowired
	EmployeeDashboardService employeeDashboardService;

	@GetMapping
	public EmployeeDashboardVO dashboard( @CurrentUser TokenParseResponseVO parseVO) {
	    return employeeDashboardService.getDashboard(parseVO);
	}
}