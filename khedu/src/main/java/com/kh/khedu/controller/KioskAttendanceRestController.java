package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.service.attendance.AttendanceService;
import com.kh.khedu.vo.attendance.KioskAttendanceRequestVO;
import com.kh.khedu.vo.attendance.KioskAttendanceResponseVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "키오스크 출결 API")
@RestController
@RequestMapping("/api/employee/kiosk")
public class KioskAttendanceRestController {

	@Autowired
    private AttendanceService attendanceService;

    @Operation(summary = "키오스크 학생 출결 체크 (직원 로그인 전용)")
    @PostMapping("/check")
    public ResponseEntity<KioskAttendanceResponseVO> checkAttendance(
            @CurrentUser TokenParseResponseVO parseVO,
            @RequestBody KioskAttendanceRequestVO request) {

        // [1] 로그인 토큰 파싱 및 직원 여부 확인
        if (parseVO == null || !AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
            throw new WhoAreYouException("학원 관리자/직원 로그인 후 이용 가능한 키오스크입니다.");
        }

        // [2] 출결 비즈니스 로직 실행
        KioskAttendanceResponseVO response = attendanceService.processKioskAttendance(request);
        return ResponseEntity.ok(response);
    }
}