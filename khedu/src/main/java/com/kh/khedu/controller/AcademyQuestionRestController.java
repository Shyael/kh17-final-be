package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.QuestionService;
import com.kh.khedu.vo.exam.StudentQuestionVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 문제 관리(학생/학부모)")
@RestController
@RequestMapping("/api/academy/question")

public class AcademyQuestionRestController {
	 	@Autowired
	    private QuestionService questionService;
	    
	    //학생용 시험 문제 목록 조회
	    @Operation(summary = "학생 시험 문제 목록 조회")
	    @GetMapping("/attempt/{attemptNo}")
	    public List<StudentQuestionVO> selectListByAttempt(
	            @PathVariable int attemptNo,
	            @CurrentUser TokenParseResponseVO parseVO) {

	        return questionService.selectListByAttempt(
	                attemptNo,
	                parseVO.getNoType()
	        );
	    }
}
