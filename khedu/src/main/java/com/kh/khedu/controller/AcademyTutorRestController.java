package com.kh.khedu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;
import com.kh.khedu.vo.tutor.TutorSearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "강사 정보 관리 서비스(학생/학부모/비로그인)")
@RestController
@RequestMapping("/api/academy/tutor")
public class AcademyTutorRestController {

	@Autowired
	private TutorService tutorService;


	// ==================== 강사 기본정보 ====================
	
	
	@Operation(summary = "강사 목록 조회 + 검색 + 페이지네이션")
	@ApiResponse(responseCode = "200", description = "강사 목록 조회 성공")
	@GetMapping
	public PageResponseVO<TutorListVO> selectList(
	        @ModelAttribute TutorSearchVO search) {
	    return tutorService.selectList(search);
	}
	
	@Operation(summary = "강사 상세정보 조회")
	@ApiResponse(responseCode = "200", description = "강사 상세정보 조회 성공")
	@GetMapping(value = "/{tutorNo}", produces = "application/json")
	public TutorDetailVO selectDetail(
			@PathVariable int tutorNo) {

		return tutorService.selectDetail(tutorNo);
	}
	

	


	

}