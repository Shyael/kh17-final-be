package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "강좌 정보 관리 서비스")
@RestController
@RequestMapping("/api/course")
public class CourseRestController {
	
	@Autowired
	private CourseDao courseDao;
	
	// 로그인한 강사의 진행중인 강의 목록 조회
	@Operation(summary = "내가 수업중인 강의 목록 조회")
	@GetMapping("/employee")
	public List<CourseDto> selectListByEmployee(
	        @CurrentUser TokenParseResponseVO parseVO) {
	    return courseDao.selectTeachingListByEmployee(
	            parseVO.getNoType());
	}
}
