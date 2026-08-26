package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dto.TutorCareerDto;
import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.dto.TutorSubjectDto;
import com.kh.khedu.service.TutorService;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "강사 정보 관리 서비스")
@RestController
@RequestMapping("/api/tutor")
public class TutorController {

	@Autowired
	private TutorService tutorService;


	// ==================== 강사 기본정보 ====================

	@ApiResponse(responseCode = "200", description = "강사 등록 성공")
	@PostMapping(value = "/", produces = "application/json")
	public void insert(@RequestBody TutorDto tutorDto) {
		tutorService.insert(tutorDto);
	}

	@ApiResponse(responseCode = "200", description = "강사 전체 목록 조회 성공")
	@GetMapping(value = "/", produces = "application/json")
	public List<TutorListVO> selectList() {
		return tutorService.selectList();
	}

	@ApiResponse(responseCode = "200", description = "과목별 강사 목록 조회 성공")
	@GetMapping(value = "/subject/{academySubjectNo}", produces = "application/json")
	public List<TutorListVO> selectListBySubject(
			@PathVariable int academySubjectNo) {

		return tutorService.selectListBySubject(academySubjectNo);
	}

	@ApiResponse(responseCode = "200", description = "강사 상세정보 조회 성공")
	@GetMapping(value = "/{tutorNo}", produces = "application/json")
	public TutorDetailVO selectDetail(
			@PathVariable int tutorNo) {

		return tutorService.selectDetail(tutorNo);
	}

	@ApiResponse(responseCode = "200", description = "강사 정보 수정 성공")
	@PutMapping(value = "/{tutorNo}", produces = "application/json")
	public TutorDto update(
			@PathVariable int tutorNo,
			@RequestBody TutorDto tutorDto) {

		return tutorService.update(tutorNo, tutorDto);
	}

	@ApiResponse(responseCode = "200", description = "강사 삭제 성공")
	@DeleteMapping(value = "/{tutorNo}", produces = "application/json")
	public boolean delete(
			@PathVariable int tutorNo) {

		return tutorService.delete(tutorNo);
	}


	// ==================== 강사 등록 가능 직원 ====================

	@ApiResponse(responseCode = "200", description = "강사 등록 가능 직원 목록 조회 성공")
	@GetMapping(value = "/available-employee", produces = "application/json")
	public List<TutorEmployeeVO> selectAvailableEmployeeList() {
		return tutorService.selectAvailableEmployeeList();
	}


	// ==================== 강사 담당과목 ====================

	@ApiResponse(responseCode = "200", description = "강사 담당과목 등록 성공")
	@PostMapping(value = "/subject", produces = "application/json")
	public void insertSubject(
			@RequestBody TutorSubjectDto tutorSubjectDto) {

		tutorService.insertSubject(tutorSubjectDto);
	}

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

	@ApiResponse(responseCode = "200", description = "강사 담당과목 삭제 성공")
	@DeleteMapping(value = "/subject/{tutorSubjectNo}", produces = "application/json")
	public boolean deleteSubject(
			@PathVariable int tutorSubjectNo) {

		return tutorService.deleteSubject(tutorSubjectNo);
	}


	// ==================== 강사 학력/경력 ====================

	@ApiResponse(responseCode = "200", description = "강사 학력/경력 등록 성공")
	@PostMapping(value = "/career", produces = "application/json")
	public void insertCareer(
			@RequestBody TutorCareerDto tutorCareerDto) {

		tutorService.insertCareer(tutorCareerDto);
	}

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

	@ApiResponse(responseCode = "200", description = "강사 학력/경력 삭제 성공")
	@DeleteMapping(value = "/career/{tutorCareerNo}", produces = "application/json")
	public boolean deleteCareer(
			@PathVariable int tutorCareerNo) {

		return tutorService.deleteCareer(tutorCareerNo);
	}

}