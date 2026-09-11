package com.kh.khedu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dto.AcademyDto;
import com.kh.khedu.dto.AcademyHistoryDto;
import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.service.AcademyService;
import com.kh.khedu.vo.academy.AcademyDetailResponseVO;
import com.kh.khedu.vo.consult.ConsultReservationInsertRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationInsertResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학원 정보 관리 서비스(학생/학부모/비로그인)")
@RestController
@RequestMapping("/api/academy")
public class AcademyRestController {

	@Autowired
	private AcademyService academyService;


	// ==================== 학원 전체정보 ====================
	@Operation(summary = "학원 전체 정보 조회")
	@ApiResponse(responseCode = "200", description = "학원 전체정보 조회 성공")
	@GetMapping(value = "/", produces = "application/json")
	public AcademyDetailResponseVO selectDetail() {
		return academyService.selectDetail();
	}

	// ==================== 상담 신청 ====================
	@Operation(summary = "상담 예약 등록")
	@ApiResponse(responseCode = "200", description = "상담 예약 등록 성공")
	@PutMapping(value = "/reservation", produces = "application/json")
	public ConsultReservationInsertResponseVO insertReservation(
			@RequestBody ConsultReservationInsertRequestVO request) {
		return academyService.insertReservation(request);
	}
}