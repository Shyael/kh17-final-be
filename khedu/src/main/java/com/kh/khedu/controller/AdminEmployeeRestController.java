package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.EmployeeService;
import com.kh.khedu.vo.admin.employee.AdminEmployeeDetailVO;
import com.kh.khedu.vo.admin.employee.AdminEmployeeListVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "관리자 직원 정보 관리")
@RestController
@RequestMapping("/api/admin/employee")
public class AdminEmployeeRestController {
	
	@Autowired
	private EmployeeService employeeService;
	
	//직원 목록
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/list", produces = "application/json")
	public List<AdminEmployeeListVO> list() {
		return employeeService.findEmployeeList();
	}
	
	//직원 상세
	@ApiResponse(responseCode = "200", description = "상세조회 성공")
	@GetMapping(value = "/detail/{employeeNo}", produces = "application/json")
	public AdminEmployeeDetailVO detail(@PathVariable int employeeNo) {
		return employeeService.findEmployeeInfo(employeeNo);
	}
	
	//직원 수정
	
	
}
