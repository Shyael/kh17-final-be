package com.kh.khedu.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.service.workschedule.EmployeeWorkScheduleService;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleAddResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleSearchResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "직원 근무 일정 관련 컨트롤러")
@RestController
@RequestMapping("/api/workSchedule")
public class EmployeeWorkScheduleRestController {

    @Autowired
    private EmployeeWorkScheduleService employeeWorkScheduleService;
    @Autowired
    private EmployeeAttendanceDao employeeAttendanceDao;

    // 근무 일정 등록
    @ApiResponse(
            responseCode = "200",
            description = "직원 근무 일정 등록 성공"
    )
    @PostMapping("/add")
    public WorkScheduleAddResponseVO add(
            @RequestBody WorkScheduleAddRequestVO requestVO) {

        return employeeWorkScheduleService.add(
                requestVO);
    }


    // 근무 일정 수정
    @ApiResponse(
            responseCode = "200",
            description = "직원 근무 일정 수정 성공"
    )
    @PatchMapping("/edit")
    public void update(
            @RequestBody WorkScheduleUpdateRequestVO requestVO) {

        employeeWorkScheduleService.update(
                requestVO);
    }


    // 특정 날짜 근무 일정 조회
    @ApiResponse(
            responseCode = "200",
            description = "직원 특정 날짜 근무 일정 조회 성공"
    )
    @GetMapping("/find")
    public WorkScheduleResponseVO find(
            @RequestParam int employeeNo,
            @RequestParam Timestamp scheduledWorkDate) {

        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                employeeNo)
                        .build();

        return employeeWorkScheduleService.find(
                employeeVO,
                scheduledWorkDate);
    }


    // 직원 기간별 근무 일정 조회
    @GetMapping("/mySearch")
    public WorkScheduleSearchResponseVO mySearch(
            @RequestParam Timestamp startDate,
            @RequestParam Timestamp endDate,
            @CurrentUser TokenParseResponseVO parseVO) {

        EmployeeDetailVO employeeDetailVO =
                employeeAttendanceDao.findByAccountNo(
                        parseVO.getAccountNo()
                );

        if(employeeDetailVO == null)
            throw new TargetNotfoundException();

        return employeeWorkScheduleService.search(
                employeeDetailVO.getEmployeeNo(),
                startDate,
                endDate
        );
    }
}