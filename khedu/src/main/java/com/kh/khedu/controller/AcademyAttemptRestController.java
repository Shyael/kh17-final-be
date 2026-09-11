package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.AttemptService;
import com.kh.khedu.vo.exam.ExamResultVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 응시 관리(학생/학부모)")
@RestController
@RequestMapping("/api/academy/attempt")
public class AcademyAttemptRestController {

    @Autowired
    private AttemptService attemptService;

    // 시험 응시 시작
    @Operation(summary = "시험 응시 시작")
    @ApiResponse(responseCode = "200", description = "시험 응시 시작 성공")
    @PostMapping("/")
    public int insert(@RequestBody AttemptDto attemptDto, @CurrentUser TokenParseResponseVO parseVO) {
        // 로그인한 학생번호 설정
        attemptDto.setStudentNo(parseVO.getNoType());

        return attemptService.insert(attemptDto);
    }
    
    // 시험 최종 제출
    @Operation(summary = "시험 최종 제출")
    @PutMapping("/{attemptNo}/submit")
    public boolean submit(
            @PathVariable int attemptNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        return attemptService.submit(attemptNo,parseVO.getNoType());
    }
    
    //시험 결과 조회(학생용)
    @Operation(summary = "학생 시험 결과 조회")
    @GetMapping("/{attemptNo}/result")
    public ExamResultVO selectResult(
    		@PathVariable int attemptNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	return attemptService.selectResult(attemptNo, parseVO.getNoType());
    }
    
    
}