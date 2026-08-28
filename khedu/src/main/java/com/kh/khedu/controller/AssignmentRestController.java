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
import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.service.AssignmentService;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "과제 관리")
@RestController
@RequestMapping("/api/assignment")
public class AssignmentRestController {

    @Autowired
    private AssignmentService assignmentService;

    // 과제 등록
    @Operation(summary = "과제 등록")
    @ApiResponse(responseCode = "200", description = "과제 등록 성공")
    @PostMapping("/")
    public int insert(
            @RequestBody AssignmentDto assignmentDto,
            @CurrentUser TokenParseResponseVO parseVO) {
    	
    	
        // 로그인한 강사번호 설정
        assignmentDto.setEmployeeNo(parseVO.getNoType());
        
        System.out.println("employeeNo = " + assignmentDto.getEmployeeNo());
        System.out.println("courseNo = " + assignmentDto.getCourseNo());
    	
        return assignmentService.insert(assignmentDto);
    }


    // 전체 과제 목록 조회
    @Operation(summary = "전체 과제 목록 조회")
    @GetMapping("/")
    public List<AssignmentListVO> selectList() {
        return assignmentService.selectList();
    }


    // 과제 상세 조회
    @Operation(summary = "과제 상세 조회")
    @GetMapping("/{assignmentNo}")
    public AssignmentDetailVO selectOne(
            @PathVariable int assignmentNo) {

        return assignmentService.selectOne(assignmentNo);
    }


    // 특정 강의의 과제 목록 조회
    @Operation(summary = "특정 강의 과제 목록 조회")
    @GetMapping("/course/{courseNo}")
    public List<AssignmentListVO> selectListByCourse(
            @PathVariable int courseNo) {

        return assignmentService.selectListByCourse(courseNo);
    }


    // 로그인한 강사가 등록한 과제 목록 조회
    @Operation(summary = "내가 등록한 과제 목록 조회")
    @GetMapping("/employee")
    public List<AssignmentListVO> selectListByEmployee(
            @CurrentUser TokenParseResponseVO parseVO) {
    	//
        return assignmentService.selectListByEmployee(
                parseVO.getNoType());
    }


    // 로그인한 학생의 과제 목록 조회
    @Operation(summary = "학생 과제 목록 조회")
    @GetMapping("/student")
    public List<StudentAssignmentListVO> selectListByStudent(
            @CurrentUser TokenParseResponseVO parseVO) {

        return assignmentService.selectListByStudent(
                parseVO.getNoType());
    }


    // 과제 수정
    @Operation(summary = "과제 수정")
    @ApiResponse(responseCode = "200", description = "과제 수정 성공")
    @PutMapping("/{assignmentNo}")
    public boolean update(
            @PathVariable int assignmentNo,
            @RequestBody AssignmentDto assignmentDto) {

        assignmentDto.setAssignmentNo(assignmentNo);

        return assignmentService.update(assignmentDto);
    }


    // 과제 삭제
    @Operation(summary = "과제 삭제")
    @ApiResponse(responseCode = "200", description = "과제 삭제 성공")
    @DeleteMapping("/{assignmentNo}")
    public boolean delete(
            @PathVariable int assignmentNo) {

        return assignmentService.delete(assignmentNo);
    }

}