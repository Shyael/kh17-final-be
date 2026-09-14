package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.AcademyDashboardService;
import com.kh.khedu.vo.dashboard.AcademyDashboardVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "학생/학부모 대시보드")
@RestController
@RequestMapping("/api/academy/dashboard")
public class AcademyDashboardRestController {

    @Autowired
    AcademyDashboardService academyDashboardService;

    @GetMapping
    public AcademyDashboardVO dashboard(@CurrentUser TokenParseResponseVO parseVO) {
        return academyDashboardService.getDashboard(parseVO);
    }
}