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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.enums.RoleType;
import com.kh.khedu.service.AssignmentService;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.AssignmentSearchVO;
import com.kh.khedu.vo.assignment.AssignmentStudentSearchVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "과제 관리(학생/학부모)")
@RestController
@RequestMapping("/api/academy/assignment")
public class AcademyAssignmentRestController {

    @Autowired
    private AssignmentService assignmentService;
 
    // 과제 상세 조회
    @Operation(summary = "과제 상세 조회")
    @GetMapping("/{assignmentNo}")
    public AssignmentDetailVO selectOne(
            @PathVariable int assignmentNo) {

        return assignmentService.selectOne(assignmentNo);
    }

    //학생 페이지네이션 + 검색 + 과제목록
    @Operation(summary = "학생 페이지네이션 + 검색 + 과제목록 조회")
    @GetMapping("/student")
    public PageResponseVO<StudentAssignmentListVO> selectStudentList(
            @ModelAttribute AssignmentStudentSearchVO search,
            @CurrentUser TokenParseResponseVO parseVO) {

        return assignmentService.selectStudentList(
                search,
                parseVO.getNoType()
        );
    }
 
    //학부모용 : 자녀 과제 목록 조회 + 검색 + 페이지네이션
    @Operation(summary = "학부모 자녀 과제 페이지네이션 + 검색 + 목록 조회")
    @GetMapping("/parent/student/{studentNo}")
    public PageResponseVO<StudentAssignmentListVO> selectListByParentStudent(
            @PathVariable int studentNo,
            @ModelAttribute AssignmentStudentSearchVO search,
            @CurrentUser TokenParseResponseVO parseVO) {

        return assignmentService.selectListByParentStudent(
                search,
                parseVO.getNoType(), // parentNo
                studentNo
        );
    }
    
    //학부모용 : 자녀 과제 상세 조회
    @GetMapping("/parent/student/{studentNo}/{assignmentNo}")
    public AssignmentDetailVO selectOneByParentStudent(
    		@PathVariable int studentNo,
    		@PathVariable int assignmentNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	return assignmentService.selectOneByParentStudent(
    			parseVO.getNoType(), // parentNo
    			studentNo,
    			assignmentNo
    	);
    }
    
    

}