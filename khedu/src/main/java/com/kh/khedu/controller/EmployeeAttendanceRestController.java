package com.kh.khedu.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.attendance.EmployeeAttendanceService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToNormalRequestVO;

import com.kh.khedu.vo.payroll.request.AttendanceLeaveRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToNormalRequestVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockInResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockOutResponseVO;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "직원 근태 관련 컨트롤러")
@CommonsApiResponse
@RestController
@RequestMapping("/api/attendance")
public class EmployeeAttendanceRestController {

    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;


    @ApiResponse(
            responseCode = "200",
            description = "직원 출근 여부 조회 성공"
    )
    @GetMapping("/working")
    public boolean working(
            @CurrentUser TokenParseResponseVO parseVO) {

        return employeeAttendanceService.working(
                parseVO);
    }
    
 // 출근
    @ApiResponse(
            responseCode = "200",
            description = "직원 출근 성공"
    )
    @PostMapping("/clockIn")
    public AttendanceClockInResponseVO clockIn(
            
            @CurrentUser TokenParseResponseVO parseVO) {

        return employeeAttendanceService.clockIn(
               
                parseVO);
    }


    // 퇴근
    @ApiResponse(
            responseCode = "200",
            description = "직원 퇴근 성공"
    )
    @PatchMapping("/clockOut")
    public AttendanceClockOutResponseVO clockOut(
           @CurrentUser TokenParseResponseVO parseVO) {

        return employeeAttendanceService.clockOut(
                parseVO);
    }


    // 결근 등록
    @ApiResponse(
            responseCode = "200",
            description = "직원 결근 등록 성공"
    )
    @PostMapping("/absent")
    public void absent(
            @RequestBody AttendanceAbsentRequestVO requestVO,
           @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.absent(
                requestVO,
                parseVO);
    }


    // 유급휴가 등록
    @ApiResponse(
            responseCode = "200",
            description = "직원 유급휴가 등록 성공"
    )
    @PostMapping("/paidLeave")
    public void paidLeave(
            @RequestBody AttendanceLeaveRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.paidLeave(
                requestVO,
                parseVO);
    }


    // 무급휴가 등록
    @ApiResponse(
            responseCode = "200",
            description = "직원 무급휴가 등록 성공"
    )
    @PostMapping("/unpaidLeave")
    public void unpaidLeave(
            @RequestBody AttendanceLeaveRequestVO requestVO,
           @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.unpaidLeave(
                requestVO,
                parseVO);
    }


    // 정상 -> 정상
    @ApiResponse(
            responseCode = "200",
            description = "정상 근태 수정 성공"
    )
    @PatchMapping("/normalToNormal")
    public void normalToNormal(
            @RequestBody AttendanceNormalToNormalRequestVO requestVO,
           @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.normalToNormal(
                requestVO,
                parseVO);
    }


    // 정상 -> 비근무
    @ApiResponse(
            responseCode = "200",
            description = "정상 근태를 비근무 상태로 변경 성공"
    )
    @PatchMapping("/normalToAbsent")
    public void normalToAbsent(
            @RequestBody AttendanceNormalToAbsentRequestVO requestVO,
           @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.normalToAbsent(
                requestVO,
                parseVO);
    }


    // 비근무 -> 정상
    @ApiResponse(
            responseCode = "200",
            description = "비근무 근태를 정상 상태로 변경 성공"
    )
    @PatchMapping("/absentToNormal")
    public void absentToNormal(
            @RequestBody AttendanceAbsentToNormalRequestVO requestVO,
           @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.absentToNormal(
                requestVO,
                parseVO);
    }


    // 비근무 -> 비근무
    @ApiResponse(
            responseCode = "200",
            description = "비근무 근태 상태 변경 성공"
    )
    @PatchMapping("/absentToAbsent")
    public void absentToAbsent(
            @RequestBody AttendanceAbsentToAbsentRequestVO requestVO,
           @CurrentUser TokenParseResponseVO parseVO) {

        employeeAttendanceService.absentToAbsent(
                requestVO,
                parseVO);
    }


}