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

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.QuestionOptionService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 보기 관리")
@RestController
@RequestMapping("/api/question-option")
public class QuestionOptionRestController {

    @Autowired
    private QuestionOptionService questionOptionService;

    // 보기 등록
    @Operation(summary = "보기 등록")
    @ApiResponse(responseCode = "200", description = "보기 등록 성공")
    @PostMapping("/")
    public int insert(
    		  @RequestBody QuestionOptionDto questionOptionDto,
              @CurrentUser TokenParseResponseVO parseVO) {
    	boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
    	
        return questionOptionService.insert(questionOptionDto, parseVO.getNoType(), tutor);
    }

    // 보기 단일 조회
    @Operation(summary = "보기 상세 조회")
    @GetMapping("/{optionNo}")
    public QuestionOptionDto selectOne(@PathVariable int optionNo) {
        return questionOptionService.selectOne(optionNo);
    }

    // 특정 문제의 보기 목록 조회
    @Operation(summary = "특정 문제 보기 목록 조회")
    @GetMapping("/question/{questionNo}")
    public List<QuestionOptionDto> selectListByQuestion(@PathVariable int questionNo) {
        return questionOptionService.selectListByQuestion(questionNo);
    }

    // 보기 수정
    @Operation(summary = "보기 수정")
    @ApiResponse(responseCode = "200", description = "보기 수정 성공")
    @PutMapping("/{optionNo}")
    public boolean update(
            @PathVariable int optionNo,
            @RequestBody QuestionOptionDto questionOptionDto,
            @CurrentUser TokenParseResponseVO parseVO) {
        questionOptionDto.setOptionNo(optionNo);
        
        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
        
        return questionOptionService.update(questionOptionDto, parseVO.getNoType(),tutor);
    }

 // 보기 삭제
    @Operation(summary = "보기 삭제")
    @ApiResponse(responseCode = "200", description = "보기 삭제 성공")
    @DeleteMapping("/{optionNo}")
    public boolean delete(
            @PathVariable int optionNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return questionOptionService.delete(optionNo, parseVO.getNoType(), tutor);
    }


    // 특정 문제의 보기 전체 삭제
    @Operation(summary = "특정 문제 보기 전체 삭제")
    @ApiResponse(responseCode = "200", description = "보기 전체 삭제 성공")
    @DeleteMapping("/question/{questionNo}")
    public boolean deleteByQuestion(
            @PathVariable int questionNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return questionOptionService.deleteByQuestion(questionNo, parseVO.getNoType(), tutor);
    }
}