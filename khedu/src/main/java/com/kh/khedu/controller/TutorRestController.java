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

import com.kh.khedu.dto.TutorCareerDto;
import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.dto.TutorSubjectDto;
import com.kh.khedu.service.TutorService;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "강사 정보 관리 서비스")
@RestController
@RequestMapping("/api/tutor")
public class TutorRestController {

	@Autowired
	private TutorService tutorService;


	// ==================== 강사 기본정보 ====================
	@Operation(summary = "강사 정보 등록")
	@ApiResponse(responseCode = "200", description = "강사 등록 성공")
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public TutorDto insert(
			@RequestPart("tutor") TutorDto tutorDto,
			@RequestPart(value = "image", required = false)
			MultipartFile image
			) throws IllegalStateException, IOException {
	    return tutorService.insert(tutorDto, image);
	}
	
	@Operation(summary = "강사 정보 목록 조회")
	@ApiResponse(responseCode = "200", description = "강사 전체 목록 조회 성공")
	@GetMapping(value = "/", produces = "application/json")
	public List<TutorListVO> selectList() {
		return tutorService.selectList();
	}	
	
	@Operation(summary = "과목별 강사 목록 조회")
	@ApiResponse(responseCode = "200", description = "과목별 강사 목록 조회 성공")
	@GetMapping(value = "/subject/{academySubjectNo}", produces = "application/json")
	public List<TutorListVO> selectListBySubject(
			@PathVariable int academySubjectNo) {

		return tutorService.selectListBySubject(academySubjectNo);
	}
	
	@Operation(summary = "강사 상세정보 조회")
	@ApiResponse(responseCode = "200", description = "강사 상세정보 조회 성공")
	@GetMapping(value = "/{tutorNo}", produces = "application/json")
	public TutorDetailVO selectDetail(
			@PathVariable int tutorNo) {

		return tutorService.selectDetail(tutorNo);
	}
	
	@Operation(summary = "강사 정보 수정")
	@ApiResponse(responseCode = "200", description = "강사 정보 수정 성공")
	@PutMapping(value = "/{tutorNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public TutorDto update(
		@PathVariable int tutorNo,
		@RequestPart("tutor") TutorDto tutorDto,
		@RequestPart(value= "image" , required = false)
		MultipartFile image
	) throws IllegalStateException, IOException {

		return tutorService.update(tutorNo, tutorDto, image);
	}
	
	@Operation(summary = "강사 정보 삭제 ")
	@ApiResponse(responseCode = "200", description = "강사 정보 삭제 성공")
	@DeleteMapping(value = "/{tutorNo}", produces = "application/json")
	public boolean delete(
			@PathVariable int tutorNo) {

		return tutorService.delete(tutorNo);
	}


	// ==================== 강사 등록 가능 직원 ====================
	
	@Operation(summary = "강사 등록 가능 직원 목록 조회")
	@ApiResponse(responseCode = "200", description = "강사 등록 가능 직원 목록 조회 성공")
	@GetMapping(value = "/available-employee", produces = "application/json")
	public List<TutorEmployeeVO> selectAvailableEmployeeList() {
		return tutorService.selectAvailableEmployeeList();
	}


	// ==================== 강사 담당과목 ====================
	
	@Operation(summary = "강사 담당과목 등록")
	@ApiResponse(responseCode = "200", description = "강사 담당과목 등록 성공")
	@PostMapping(value = "/subject", produces = "application/json")
	public void insertSubject(
			@RequestBody TutorSubjectDto tutorSubjectDto) {

		tutorService.insertSubject(tutorSubjectDto);
	}
	
	@Operation(summary = "강사 담당과목 수정")
	@ApiResponse(responseCode = "200", description = "강사 담당과목 수정 성공")
	@PutMapping(value = "/subject/{tutorSubjectNo}", produces = "application/json")
	public TutorSubjectDto updateSubject(
			@PathVariable int tutorSubjectNo,
			@RequestBody TutorSubjectDto tutorSubjectDto) {

		return tutorService.updateSubject(
				tutorSubjectNo,
				tutorSubjectDto
		);
	}
	
	@Operation(summary = "강사 담당과목 삭제")
	@ApiResponse(responseCode = "200", description = "강사 담당과목 삭제 성공")
	@DeleteMapping(value = "/subject/{tutorSubjectNo}", produces = "application/json")
	public boolean deleteSubject(
			@PathVariable int tutorSubjectNo) {

		return tutorService.deleteSubject(tutorSubjectNo);
	}


	// ==================== 강사 학력/경력 ====================
	
	@Operation(summary = "강사 학력/경력 등록")
	@ApiResponse(responseCode = "200", description = "강사 학력/경력 등록 성공")
	@PostMapping(value = "/career", produces = "application/json")
	public void insertCareer(
			@RequestBody TutorCareerDto tutorCareerDto) {

		tutorService.insertCareer(tutorCareerDto);
	}
	
	@Operation(summary = "강사 학력/경력 수정")
	@ApiResponse(responseCode = "200", description = "강사 학력/경력 수정 성공")
	@PutMapping(value = "/career/{tutorCareerNo}", produces = "application/json")
	public TutorCareerDto updateCareer(
			@PathVariable int tutorCareerNo,
			@RequestBody TutorCareerDto tutorCareerDto) {

		return tutorService.updateCareer(
				tutorCareerNo,
				tutorCareerDto
		);
	}
	
	@Operation(summary = "강사 학력/경력 삭제")
	@ApiResponse(responseCode = "200", description = "강사 학력/경력 삭제 성공")
	@DeleteMapping(value = "/career/{tutorCareerNo}", produces = "application/json")
	public boolean deleteCareer(
			@PathVariable int tutorCareerNo) {

		return tutorService.deleteCareer(tutorCareerNo);
	}
	
	//강사 이미지 삭제
	@Operation(summary = "강사 이미지 삭제")
	@ApiResponse(responseCode = "200", description = "강사 이미지 삭제 성공")
	@DeleteMapping("/{tutorNo}/image")
	public void deleteImage(@PathVariable int tutorNo) {
		tutorService.deleteImage(tutorNo);
	}

}