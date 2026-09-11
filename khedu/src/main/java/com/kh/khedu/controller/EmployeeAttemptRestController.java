package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.AttemptService;
import com.kh.khedu.vo.exam.ExamResultVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 응시 관리(직원)")
@RestController
@RequestMapping("/api/employee/attempt")
public class EmployeeAttemptRestController {
	
	@Autowired
    private AttemptService attemptService;
	
	// 강사용 학생 시험 결과 조회
    @Operation(summary = "강사용 학생 시험 결과 조회")
    @GetMapping("/{attemptNo}/result")
    public ExamResultVO selectResultByManage(
            @PathVariable int attemptNo,
            @CurrentUser TokenParseResponseVO parseVO) {
    	//강사인지
        boolean tutor =parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
        
        return attemptService.selectResultByManage(attemptNo,parseVO.getNoType(),tutor);
    }
}
