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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학원 정보 관리 서비스")
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


	// ==================== 학원 기본정보 ====================
	@Operation(summary = "학원 정보 등록")
	@ApiResponse(responseCode = "200", description = "학원 기본정보 등록 성공")
	@PostMapping( 
		value = "/",
	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public int insert(
			@RequestPart("academy") AcademyDto academyDto,
	        @RequestPart(value = "images", required = false)
	        List<MultipartFile> images
	) throws IllegalStateException, IOException {
		return academyService.insert(academyDto,images);
	}
	
	@Operation(summary = "학원 정보 수정")
	@ApiResponse(responseCode = "200", description = "학원 기본정보 수정 성공")
	@PutMapping(
	    value = "/",
	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public void update(
	        @RequestPart("academy") AcademyDto academyDto,
	        @RequestPart(value = "images", required = false)
	        List<MultipartFile> images
	) throws IllegalStateException, IOException {
	    academyService.update(
	            academyDto,
	            images
	    );
	}
	
	@Operation(summary = "학원 정보 삭제")
	@ApiResponse(responseCode = "200", description = "학원 기본정보 삭제 성공")
	@DeleteMapping(value = "/", produces = "application/json")
	public boolean delete() {
		return academyService.delete();
	}


	// ==================== 학원 연혁 ====================

	@Operation(summary = "학원 연혁 등록")
	@ApiResponse(responseCode = "200", description = "학원 연혁 등록 성공")
	@PostMapping(value = "/history", produces = "application/json")
	public void insertHistory(
			@RequestBody AcademyHistoryDto academyHistoryDto) {

		academyService.insertHistory(academyHistoryDto);
	}
	
	@Operation(summary = "학원 연혁 수정")
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
	
	@Operation(summary = "학원 연혁 삭제")
	@ApiResponse(responseCode = "200", description = "학원 연혁 삭제 성공")
	@DeleteMapping(value = "/history/{academyHistoryNo}", produces = "application/json")
	public boolean deleteHistory(
			@PathVariable int academyHistoryNo) {

		return academyService.deleteHistory(academyHistoryNo);
	}


	// ==================== 학원 과목 ====================
	
	@Operation(summary = "학원 과목 등록")
	@ApiResponse(responseCode = "200", description = "학원 과목 등록 성공")
	@PostMapping(value = "/subject", produces = "application/json")
	public void insertSubject(
			@RequestBody AcademySubjectDto academySubjectDto) {

		academyService.insertSubject(academySubjectDto);
	}
	
	@Operation(summary = "학원 과목 수정")
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
	
	@Operation(summary = "학원 과목 삭제")
	@ApiResponse(responseCode = "200", description = "학원 과목 삭제 성공")
	@DeleteMapping(value = "/subject/{academySubjectNo}", produces = "application/json")
	public boolean deleteSubject(
			@PathVariable int academySubjectNo) {

		return academyService.deleteSubject(academySubjectNo);
	}
	
	@Operation(summary = "이미지 삭제")
	@DeleteMapping("/{academyNo}/image/{attachNo}")
	public void deleteImage(
	        @PathVariable int academyNo,
	        @PathVariable int attachNo
	) {
	    academyService.deleteImage(
	            academyNo,
	            attachNo
	    );
	}

}