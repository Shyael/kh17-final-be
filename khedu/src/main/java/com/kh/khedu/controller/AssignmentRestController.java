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
import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.enums.RoleType;
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
    @PostMapping(
            value = "/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
    public int insert(
    		@RequestPart("assignment") AssignmentDto assignmentDto,

            @RequestPart(value = "files", required = false) 
    		List<MultipartFile> files,

            @CurrentUser
            TokenParseResponseVO parseVO
    	) throws IllegalStateException, IOException {
    	
    	
        // 로그인한 강사번호 설정
        assignmentDto.setEmployeeNo(parseVO.getNoType());
        
    	
        return assignmentService.insert(assignmentDto, files);
    }

    //직원용 과제 목록 조회
    @Operation(summary = "직원용 과제 목록 조회")
    @GetMapping("/manage")
    public List<AssignmentListVO> selectManageList(
    		@CurrentUser TokenParseResponseVO parseVO){
    	
    	// 강사라면 본인이 작성한 과제만
        if (parseVO.getRoleNames().contains(RoleType.TUTOR.getCode())) {
            return assignmentService.selectListByEmployee(
                    parseVO.getNoType());
        }

        // 원장, 데스크 등은 전체 과제
        return assignmentService.selectList();
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
    @PutMapping(
            value = "/{assignmentNo}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
    public boolean update(
    		 @PathVariable int assignmentNo,

             @RequestPart("assignment") AssignmentDto assignmentDto,

             @RequestPart(value = "files", required = false)
             List<MultipartFile> files,

             @CurrentUser
             TokenParseResponseVO parseVO
     ) throws IllegalStateException, IOException {

        assignmentDto.setAssignmentNo(assignmentNo);
        
        // 강사 여부
        boolean tutor =
                parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return assignmentService.update(
                assignmentDto,
                files,
                parseVO.getNoType(),
                tutor
        );
    }


    // 과제 삭제
    @Operation(summary = "과제 삭제")
    @ApiResponse(responseCode = "200", description = "과제 삭제 성공")
    @DeleteMapping("/{assignmentNo}")
    public boolean delete(
            @PathVariable int assignmentNo,
            @CurrentUser TokenParseResponseVO parseVO) {
    	
        boolean tutor =
                parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return assignmentService.delete(
                assignmentNo,
                parseVO.getNoType(),
                tutor
        );
    }

    // 과제 첨부파일 삭제
    @Operation(summary = "과제 첨부파일 삭제")
    @ApiResponse(responseCode = "200", description = "과제 첨부파일 삭제 성공")
    @DeleteMapping("/{assignmentNo}/file/{attachNo}")
    public void deleteFile(
            @PathVariable int assignmentNo,
            @PathVariable int attachNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor =
                parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        assignmentService.deleteFile(
                assignmentNo,
                attachNo,
                parseVO.getNoType(),
                tutor
        );
    }
    
    //학부모용  : 자녀 과제 목록 조회
    @GetMapping("/parent/student/{studentNo}")
    public List<StudentAssignmentListVO> selectListByParentStudent(
    		@PathVariable int studentNo,
    		@CurrentUser TokenParseResponseVO parseVO){
    	return assignmentService.selectListByParentStudent(
    			parseVO.getNoType(),//parentNo
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