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

import com.kh.khedu.dto.AttemptAnswerDto;
import com.kh.khedu.service.AttemptAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 답안 관리")
@RestController
@RequestMapping("/api/attempt-answer")
public class AttemptAnswerRestController {

    @Autowired
    private AttemptAnswerService attemptAnswerService;


    // 답안 등록
    @Operation(summary = "시험 답안 등록")
    @ApiResponse(responseCode = "200", description = "시험 답안 등록 성공")
    @PostMapping("/")
    public void insert(@RequestBody AttemptAnswerDto attemptAnswerDto) {
        attemptAnswerService.insert(attemptAnswerDto);
    }


    // 특정 응시의 특정 문제 답안 조회
    @Operation(summary = "특정 문제 답안 조회")
    @GetMapping("/attempt/{attemptNo}/question/{questionNo}")
    public AttemptAnswerDto selectOne(@PathVariable int attemptNo, @PathVariable int questionNo) {
        return attemptAnswerService.selectOne(attemptNo, questionNo);
    }


    // 특정 응시의 전체 답안 목록 조회
    @Operation(summary = "특정 응시 전체 답안 조회")
    @GetMapping("/attempt/{attemptNo}")
    public List<AttemptAnswerDto> selectListByAttempt(@PathVariable int attemptNo) {
    	return attemptAnswerService.selectListByAttempt(attemptNo);
    }


    // 답안 수정
    @Operation(summary = "시험 답안 수정")
    @ApiResponse(responseCode = "200", description = "시험 답안 수정 성공")
    @PutMapping("/attempt/{attemptNo}/question/{questionNo}")
    public boolean update(
            @PathVariable int attemptNo,
            @PathVariable int questionNo,
            @RequestBody AttemptAnswerDto attemptAnswerDto) {
        attemptAnswerDto.setAttemptNo(attemptNo);
        attemptAnswerDto.setQuestionNo(questionNo);
        return attemptAnswerService.update(attemptAnswerDto);
    }


    // 특정 답안 삭제
    @Operation(summary = "시험 답안 삭제")
    @ApiResponse(responseCode = "200", description = "시험 답안 삭제 성공")
    @DeleteMapping("/attempt/{attemptNo}/question/{questionNo}")
    public boolean delete(@PathVariable int attemptNo, @PathVariable int questionNo) {
        return attemptAnswerService.delete(attemptNo, questionNo);
    }


    // 특정 응시의 전체 답안 삭제
    @Operation(summary = "특정 응시 전체 답안 삭제")
    @ApiResponse(responseCode = "200", description = "전체 답안 삭제 성공")
    @DeleteMapping("/attempt/{attemptNo}")
    public boolean deleteByAttempt(@PathVariable int attemptNo) {
        return attemptAnswerService.deleteByAttempt(attemptNo);
    }
}