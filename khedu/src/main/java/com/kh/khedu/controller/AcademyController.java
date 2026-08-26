package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dto.AcademyDto;
import com.kh.khedu.dto.AcademyHistoryDto;
import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.service.AcademyService;
import com.kh.khedu.vo.academy.AcademyDetailResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학원 정보 관리 서비스")
@RestController
@RequestMapping("/service/academy")
public class AcademyController {

	@Autowired
	private AcademyService academyService;


	// ==================== 학원 전체정보 ====================

	@ApiResponse(responseCode = "200", description = "학원 전체정보 조회 성공")
	@GetMapping(value = "/", produces = "application/json")
	public AcademyDetailResponseVO selectDetail() {
		return academyService.selectDetail();
	}


	// ==================== 학원 기본정보 ====================

	@ApiResponse(responseCode = "200", description = "학원 기본정보 등록 성공")
	@PostMapping(value = "/", produces = "application/json")
	public void insert(@RequestBody AcademyDto academyDto) {
		academyService.insert(academyDto);
	}

	@ApiResponse(responseCode = "200", description = "학원 기본정보 수정 성공")
	@PutMapping(value = "/", produces = "application/json")
	public boolean update(@RequestBody AcademyDto academyDto) {
		return academyService.update(academyDto);
	}

	@ApiResponse(responseCode = "200", description = "학원 기본정보 삭제 성공")
	@DeleteMapping(value = "/", produces = "application/json")
	public boolean delete() {
		return academyService.delete();
	}


	// ==================== 학원 연혁 ====================

	@ApiResponse(responseCode = "200", description = "학원 연혁 등록 성공")
	@PostMapping(value = "/history", produces = "application/json")
	public void insertHistory(
			@RequestBody AcademyHistoryDto academyHistoryDto) {

		academyService.insertHistory(academyHistoryDto);
	}

	@ApiResponse(responseCode = "200", description = "학원 연혁 수정 성공")
	@PutMapping(value = "/history/{academyHistoryNo}", produces = "application/json")
	public AcademyHistoryDto updateHistory(
			@RequestBody AcademyHistoryDto academyHistoryDto,
			@PathVariable int academyHistoryNo) {

		return academyService.updateHistory(
				academyHistoryNo,
				academyHistoryDto
		);
	}

	@ApiResponse(responseCode = "200", description = "학원 연혁 삭제 성공")
	@DeleteMapping(value = "/history/{academyHistoryNo}", produces = "application/json")
	public boolean deleteHistory(
			@PathVariable int academyHistoryNo) {

		return academyService.deleteHistory(academyHistoryNo);
	}


	// ==================== 학원 과목 ====================

	@ApiResponse(responseCode = "200", description = "학원 과목 등록 성공")
	@PostMapping(value = "/subject", produces = "application/json")
	public void insertSubject(
			@RequestBody AcademySubjectDto academySubjectDto) {

		academyService.insertSubject(academySubjectDto);
	}

	@ApiResponse(responseCode = "200", description = "학원 과목 수정 성공")
	@PutMapping(value = "/subject/{academySubjectNo}", produces = "application/json")
	public AcademySubjectDto updateSubject(
			@RequestBody AcademySubjectDto academySubjectDto,
			@PathVariable int academySubjectNo) {

		return academyService.updateSubject(
				academySubjectNo,
				academySubjectDto
		);
	}

	@ApiResponse(responseCode = "200", description = "학원 과목 삭제 성공")
	@DeleteMapping(value = "/subject/{academySubjectNo}", produces = "application/json")
	public boolean deleteSubject(
			@PathVariable int academySubjectNo) {

		return academyService.deleteSubject(academySubjectNo);
	}

}