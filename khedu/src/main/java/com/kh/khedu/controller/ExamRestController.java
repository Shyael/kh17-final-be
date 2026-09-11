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
import com.kh.khedu.vo.exam.ExamSearchVO;
import com.kh.khedu.vo.exam.ExamStatisticsVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;
import com.kh.khedu.vo.exam.StudentExamSearchVO;
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

    	  boolean tutor = parseVO.getRoleNames()
    	            .contains(RoleType.TUTOR.getCode());

    	  return examService.insert(
    	            examDto,
    	            parseVO.getNoType(),
    	            tutor
    	  );
    }

    //강사/관리자 시험 목록
    @Operation(summary = "시험 관리 목록 조회")
    @GetMapping("/manage")
    public PageResponseVO<ExamListVO> selectManageList(
            @ModelAttribute ExamSearchVO search,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor = parseVO.getRoleNames()
                .contains(RoleType.TUTOR.getCode());

        return examService.selectManageList(
                search,
                parseVO.getNoType(), //employeeNo
                tutor
        );
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
    
    // 강사용 시험 응시자 목록 조회
    @Operation(summary = "시험 응시자 목록 조회")
    @GetMapping("/{examNo}/attempts")
    public List<ExamAttemptListVO> selectAttemptList(
            @PathVariable int examNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return examService.selectAttemptList(examNo, parseVO.getNoType(), tutor);
    }
    
    // 시험 문제 일괄 임시저장
    @Operation(summary = "시험 문제 일괄 임시저장")
    @ApiResponse(responseCode = "200", description = "시험 문제 임시저장 성공")
    @PutMapping("/{examNo}/draft")
    public ExamDraftRequestVO saveDraft(
            @PathVariable int examNo,
            @RequestBody ExamDraftRequestVO request,
            @CurrentUser TokenParseResponseVO parseVO) {
        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
        
        return examService.saveDraft(
                examNo,
                request,
                parseVO.getNoType(),
                tutor
        );
    }
    
    //시험 통계 조회
    @Operation(summary = "시험 통계 조회")
    @GetMapping("/{examNo}/statistics")
    public ExamStatisticsVO selectStatistics(
    		@PathVariable int examNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
    	
    	return examService.selectStatistics(
    			examNo,
    			parseVO.getNoType(),
    			tutor);
    }
}