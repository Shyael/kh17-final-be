package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dto.AssignmentSubmitDto;
import com.kh.khedu.service.AssignmentSubmitService;
import com.kh.khedu.vo.assignment.AssignmentSubmitDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitListVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitStudentListVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "과제 제출 관리(직원)")
@RestController
@RequestMapping("/api/employee/assignment-submit")
public class EmployeeAssignmentSubmitRestController {
	
	@Autowired
    private AssignmentSubmitService assignmentSubmitService;
	
	// 전체 과제 제출 목록 조회
    @Operation(summary = "전체 과제 제출 목록 조회")
    @GetMapping("/")
    public List<AssignmentSubmitListVO> selectList() {
        return assignmentSubmitService.selectList();
    }


    // 특정 과제 제출 상세 조회
    @Operation(summary = "과제 제출 상세 조회")
    @GetMapping("/{submitNo}")
    public AssignmentSubmitDetailVO selectOne(
            @PathVariable int submitNo) {
        return assignmentSubmitService.selectOne(submitNo);
    }
    
    // 강사용 : 특정 과제의 제출한 학생 목록 조회
    @Operation(summary = "특정 과제 제출 목록 조회")
    @GetMapping("/assignment/{assignmentNo}")
    public List<AssignmentSubmitListVO> selectListByAssignment(
            @PathVariable int assignmentNo) {

        return assignmentSubmitService
                .selectListByAssignment(assignmentNo);
    }


    // 강사용 : 특정 과제의 전체 수강생 제출 현황 조회(이걸 쓸꺼임)
    @Operation(summary = "특정 과제 전체 학생 제출 현황 조회")
    @GetMapping("/assignment/{assignmentNo}/students")
    public List<AssignmentSubmitStudentListVO> selectStudentListByAssignment(
            @PathVariable int assignmentNo) {

        return assignmentSubmitService
                .selectStudentListByAssignment(assignmentNo);
    }
    
    // 강사용 : 제출 피드백 등록 및 수정
    @Operation(summary = "과제 제출 피드백 등록 및 수정")
    @ApiResponse(responseCode = "200", description = "피드백 등록 및 수정 성공")
    @PutMapping("/{submitNo}/comment")
    public boolean updateComment(
            @PathVariable int submitNo,
            @RequestBody AssignmentSubmitDto assignmentSubmitDto) {

        assignmentSubmitDto.setSubmitNo(submitNo);

        return assignmentSubmitService.updateComment(assignmentSubmitDto);
    }
}
