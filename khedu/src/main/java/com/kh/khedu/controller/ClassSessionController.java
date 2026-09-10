package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.ClassSessionService;
import com.kh.khedu.vo.classSession.AdminClassSessionInsertVO;
import com.kh.khedu.vo.classSession.AdminClassSessionStatusVO;
import com.kh.khedu.vo.classSession.ClassSessionEndRequestVO;
import com.kh.khedu.vo.classSession.ClassSessionStartRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "실제 수업 관리")
@RestController
@RequestMapping("/api/employee/class-session")
public class ClassSessionController {
	
	@Autowired
	private ClassSessionService classSessionService;
	
	//세션 시작
	@ApiResponse(responseCode = "200", description = "세션 자동 생성 성공")
	@PostMapping("/start")
	public void startClass(@RequestBody ClassSessionStartRequestVO request) {
		classSessionService.StartClass(request);
	}
	
	//세션 종료
	@ApiResponse(responseCode = "200", description = "세션 자동 종료 성공")
	@PostMapping("/end")
	public void endClass(@RequestBody ClassSessionEndRequestVO request) {
		classSessionService.EndClass(request);
	}
	
	/**
     * 수업 세션 사후 등록
     * - 원장/데스크/관리자: 모든 스케줄 등록 가능
     * - 강사: 본인이 담당하는 스케줄만 등록 가능
     */
	@PostMapping
    public ResponseEntity<String> insertSession(
            @CurrentUser TokenParseResponseVO parseVO,
            @RequestBody AdminClassSessionInsertVO request) {
        
        classSessionService.insertSessionByAdmin(request, parseVO);
        return ResponseEntity.ok("수업 세션이 성공적으로 등록되었습니다");
    }
	
	/**
     * 세션 상태 변경 (진행중 / 종료 / 취소)
     * - 원장/데스크/관리자: 모든 세션 상태 변경 가능
     * - 강사: 본인이 담당하는 세션만 상태 변경 가능
     */
    @PatchMapping("/status")
    public ResponseEntity<String> updateStatus(
            @CurrentUser TokenParseResponseVO parseVO,
            @RequestBody AdminClassSessionStatusVO request) {
        
        classSessionService.updateStatusByAdmin(request, parseVO);
        return ResponseEntity.ok("세션 상태가 성공적으로 변경되었습니다");
    }
}
