package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.exam.ExamAttemptListVO;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamDraftRequestVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.ExamResultVO;
import com.kh.khedu.vo.exam.ExamSearchVO;
import com.kh.khedu.vo.exam.ExamStatisticsVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;
import com.kh.khedu.vo.exam.StudentExamSearchVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시험 관리(학생/학부모)")
@RestController
@RequestMapping("/api/academy/exam")
public class AcademyExamRestController {

    @Autowired
    private ExamService examService;

    //학생용 시험 단일 조회
    @Operation(summary = "학생 시험 상세 조회")
    @GetMapping("/student/{examNo}")
    public StudentExamDetailVO selectDetailByStudent(
    		@PathVariable int examNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	return examService.selectDetailByStudent(examNo, parseVO.getNoType());
    }

    //학생 시험 목록
    @Operation(summary = "학생 시험 목록 조회")
    @GetMapping("/student")
    public PageResponseVO<StudentExamListVO> selectStudentList(
            @ModelAttribute StudentExamSearchVO search,
            @CurrentUser TokenParseResponseVO parseVO) {
        return examService.selectStudentList(
                search,
                parseVO.getNoType() //studentNo
        );
    }

    // 학부모 - 자녀 시험 목록
    @Operation(summary = "학부모용 자녀 시험 목록 조회")
    @GetMapping("/parent/student/{studentNo}")
    public PageResponseVO<StudentExamListVO> selectParentStudentList(
            @PathVariable int studentNo,
            @ModelAttribute StudentExamSearchVO search,
            @CurrentUser TokenParseResponseVO parseVO) {

        return examService.selectParentStudentList(
                search,
                parseVO.getNoType(), // parentNo
                studentNo
        );
    }
    
    // 학부모 - 자녀 시험 상세
    @Operation(summary = "학부모용 자녀 시험 상세 조회")
    @GetMapping("/parent/student/{studentNo}/{examNo}")
    public StudentExamDetailVO selectParentStudentDetail(
            @PathVariable int studentNo,
            @PathVariable int examNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        return examService.selectParentStudentDetail(
                examNo,
                parseVO.getNoType(), // parentNo
                studentNo
        );
    }
    
    // 학부모 - 자녀 시험 결과 조회
    @Operation(summary = "학부모용 자녀 시험 결과 조회")
    @GetMapping("/parent/student/{studentNo}/attempt/{attemptNo}/result")
    public ExamResultVO selectParentStudentResult(
            @PathVariable int studentNo,
            @PathVariable int attemptNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        return examService.selectParentStudentResult(
                attemptNo,
                parseVO.getNoType(), // parentNo
                studentNo
        );
    }
    
    
}