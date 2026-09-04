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
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.service.AttemptService;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 응시 관리")
@RestController
@RequestMapping("/api/attempt")
public class AttemptRestController {

    @Autowired
    private AttemptService attemptService;


    // 시험 응시 시작
    @Operation(summary = "시험 응시 시작")
    @ApiResponse(responseCode = "200", description = "시험 응시 시작 성공")
    @PostMapping("/")
    public int insert(@RequestBody AttemptDto attemptDto, @CurrentUser TokenParseResponseVO parseVO) {
        // 로그인한 학생번호 설정
        attemptDto.setStudentNo(parseVO.getNoType());

        return attemptService.insert(attemptDto);
    }


    // 응시 단일 조회
    @Operation(summary = "시험 응시 상세 조회")
    @GetMapping("/{attemptNo}")
    public AttemptDto selectOne(@PathVariable int attemptNo) {
        return attemptService.selectOne(attemptNo);
    }


    // 학생용 : 특정 시험의 내 응시정보 조회
    @Operation(summary = "특정 시험 내 응시정보 조회")
    @GetMapping("/exam/{examNo}/me")
    public AttemptDto selectOneByExamStudent(@PathVariable int examNo, @CurrentUser TokenParseResponseVO parseVO) {
        return attemptService.selectOneByExamStudent(examNo, parseVO.getNoType());
    }


    // 강사용 : 특정 시험의 전체 응시 목록 조회
    @Operation(summary = "특정 시험 응시 목록 조회")
    @GetMapping("/exam/{examNo}")
    public List<AttemptDto> selectListByExam(@PathVariable int examNo) {
        return attemptService.selectListByExam(examNo);
    }


    // 학생용 : 내 전체 시험 응시 목록 조회
    @Operation(summary = "내 시험 응시 목록 조회")
    @GetMapping("/student")
    public List<AttemptDto> selectListByStudent(@CurrentUser TokenParseResponseVO parseVO) {
        return attemptService.selectListByStudent(parseVO.getNoType());
    }


    // 시험 제출
    @Operation(summary = "시험 제출")
    @ApiResponse(responseCode = "200", description = "시험 제출 성공")
    @PutMapping("/{attemptNo}/submit")
    public boolean submit(@PathVariable int attemptNo, @RequestBody AttemptDto attemptDto) {
        attemptDto.setAttemptNo(attemptNo);
        return attemptService.submit(attemptDto);
    }


    // 응시 상태 수정
    @Operation(summary = "시험 응시 상태 수정")
    @ApiResponse(responseCode = "200", description = "응시 상태 수정 성공")
    @PutMapping("/{attemptNo}/status")
    public boolean updateStatus( @PathVariable int attemptNo, @RequestBody AttemptDto attemptDto) {
        attemptDto.setAttemptNo(attemptNo);
        return attemptService.updateStatus(attemptDto);
    }


    // 응시 삭제
    @Operation(summary = "시험 응시 삭제")
    @ApiResponse(responseCode = "200", description = "시험 응시 삭제 성공")
    @DeleteMapping("/{attemptNo}")
    public boolean delete(@PathVariable int attemptNo) {
        return attemptService.delete(attemptNo);
    }
}