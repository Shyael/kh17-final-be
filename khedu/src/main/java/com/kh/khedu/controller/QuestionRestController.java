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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.QuestionService;
import com.kh.khedu.vo.exam.StudentQuestionVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 문제 관리")
@RestController
@RequestMapping("/api/question")
public class QuestionRestController {

    @Autowired
    private QuestionService questionService;

    // 문제 등록
    @Operation(summary = "문제 등록")
    @ApiResponse(responseCode = "200", description = "문제 등록 성공")
    @PostMapping(
            value = "/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public int insert(
            @RequestPart("question") QuestionDto questionDto,
            @RequestPart(value = "files", required = false)
            List<MultipartFile> files,
            @CurrentUser TokenParseResponseVO parseVO)
            throws IllegalStateException, IOException {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return questionService.insert(
                questionDto,
                files,
                parseVO.getNoType(),
                tutor
        );
    }

    // 문제 단일 조회
    @Operation(summary = "문제 상세 조회")
    @GetMapping("/{questionNo}")
    public QuestionDto selectOne(@PathVariable int questionNo) {
        return questionService.selectOne(questionNo);
    }

    // 특정 시험의 문제 목록 조회
    @Operation(summary = "특정 시험 문제 목록 조회")
    @GetMapping("/exam/{examNo}")
    public List<QuestionDto> selectListByExam(@PathVariable int examNo) {
        return questionService.selectListByExam(examNo);
    }
    
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

    // 문제 수정
    @Operation(summary = "문제 수정")
    @ApiResponse(responseCode = "200", description = "문제 수정 성공")
    @PutMapping(
            value = "/{questionNo}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public boolean update(
            @PathVariable int questionNo,
            @RequestPart("question") QuestionDto questionDto,
            @RequestPart(value = "files", required = false)
            List<MultipartFile> files,
            @CurrentUser TokenParseResponseVO parseVO)
            throws IllegalStateException, IOException {

        questionDto.setQuestionNo(questionNo);

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return questionService.update(
                questionDto,
                files,
                parseVO.getNoType(),
                tutor
        );
    }


    // 문제 삭제
    @Operation(summary = "문제 삭제")
    @ApiResponse(responseCode = "200", description = "문제 삭제 성공")
    @DeleteMapping("/{questionNo}")
    public boolean delete(
            @PathVariable int questionNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return questionService.delete(
                questionNo,
                parseVO.getNoType(),
                tutor
        );
    }


    // 문제 첨부파일 삭제
    @Operation(summary = "문제 첨부파일 삭제")
    @ApiResponse(responseCode = "200", description = "문제 첨부파일 삭제 성공")
    @DeleteMapping("/{questionNo}/file/{attachNo}")
    public void deleteFile(
            @PathVariable int questionNo,
            @PathVariable int attachNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        questionService.deleteFile(
                questionNo,
                attachNo,
                parseVO.getNoType(),
                tutor
        );
    }
}