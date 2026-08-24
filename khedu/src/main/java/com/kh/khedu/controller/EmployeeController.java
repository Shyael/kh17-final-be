package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.EmployeeService;
import com.kh.khedu.vo.register.EmployeeRegisterRequestVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "직원 정보 관리 서비스")
@RestController
@RequestMapping("/service/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@ApiResponse(responseCode = "200", description = "등록성공")
	@PostMapping(value ="/register", produces = "application/json")
	public void register(@RequestBody EmployeeRegisterRequestVO request){
		employeeService.registerEmployee(request);
	}
}
