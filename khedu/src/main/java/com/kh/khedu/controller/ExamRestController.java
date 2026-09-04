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
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.ExamService;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 관리")
@RestController
@RequestMapping("/api/exam")
public class ExamRestController {

    @Autowired
    private ExamService examService;

    // 시험 등록
    @Operation(summary = "시험 등록")
    @ApiResponse(responseCode = "200", description = "시험 등록 성공")
    @PostMapping("/")
    public int insert(
            @RequestBody ExamDto examDto,
            @CurrentUser TokenParseResponseVO parseVO) {

        // 로그인한 직원 번호를 출제자로 설정
        examDto.setEmployeeNo(parseVO.getNoType());

        return examService.insert(examDto);
    }

    // 전체 시험 목록 조회
    @Operation(summary = "전체 시험 목록 조회")
    @GetMapping("/")
    public List<ExamListVO> selectList() {
        return examService.selectList();
    }


    // 직원용 시험 목록 조회
    @Operation(summary = "직원용 시험 목록 조회")
    @GetMapping("/manage")
    public List<ExamListVO> selectManageList(@CurrentUser TokenParseResponseVO parseVO) {
        // 강사는 본인이 등록한 시험만 조회
        if (parseVO.getRoleNames().contains(RoleType.TUTOR.getCode())) {
        	
            return examService.selectListByEmployee(parseVO.getNoType());
        }

        // 원장, 데스크 등은 전체 시험
        return examService.selectList();
    }

    // 시험 단일 조회
    @Operation(summary = "시험 상세 조회")
    @GetMapping("/{examNo}")
    public ExamDetailVO selectDetail(
            @PathVariable int examNo,
            @CurrentUser TokenParseResponseVO parseVO) {
        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
        return examService.selectDetail(examNo, parseVO.getNoType(),tutor);
    }
    
    //학생용 시험 단일 조회
    @Operation(summary = "학생 시험 상세 조회")
    @GetMapping("/student/{examNo}")
    public StudentExamDetailVO selectDetailByStudent(
    		@PathVariable int examNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	return examService.selectDetailByStudent(examNo, parseVO.getNoType());
    }


    // 특정 강의의 시험 목록 조회
    @Operation(summary = "특정 강의 시험 목록 조회")
    @GetMapping("/course/{courseNo}")
    public List<ExamListVO> selectListByCourse(@PathVariable int courseNo) {
        return examService.selectListByCourse(courseNo);
    }
    
    // 학생용 시험 목록 조회
    @Operation(summary = "학생 시험 목록 조회")
    @GetMapping("/student")
    public List<StudentExamListVO> selectListByStudent(
    		@CurrentUser TokenParseResponseVO parseVO){
    	return examService.selectListByStudent(parseVO.getNoType());
    }

    // 시험 수정
    @Operation(summary = "시험 수정")
    @ApiResponse(responseCode = "200", description = "시험 수정 성공")
    @PutMapping("/{examNo}")
    public boolean update(
            @PathVariable int examNo,
            @RequestBody ExamDto examDto,
            @CurrentUser TokenParseResponseVO parseVO) {

        examDto.setExamNo(examNo);

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return examService.update(examDto, parseVO.getNoType(), tutor);
    }

    // 시험 삭제
    @Operation(summary = "시험 삭제")
    @ApiResponse(responseCode = "200", description = "시험 삭제 성공")
    @DeleteMapping("/{examNo}")
    public boolean delete(
            @PathVariable int examNo,
            @CurrentUser TokenParseResponseVO parseVO) {
        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
        return examService.delete(examNo, parseVO.getNoType(), tutor);
    }
}