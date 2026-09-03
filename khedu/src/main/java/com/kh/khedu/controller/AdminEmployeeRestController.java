package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.EmployeeService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "관리자 직원 정보 관리")
@RestController
@RequestMapping("/api/admin/employee")
public class AdminEmployeeRestController {
	
	@Autowired
	private EmployeeService employeeService;
	
	//직원 목록
//	@GetMapping("/")
//	public List<AdminEmployeeDetailVO> list() {
//		return employeeService.findAllEmployees();
//	}
	
	//직원 상세
	
	//직원 수정
	
	
}
