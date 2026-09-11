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
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.AssignmentSearchVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "과제 관리(직원)")
@RestController
@RequestMapping("/api/employee/assignment")
public class EmployeeAssignmentRestController {

	@Autowired
    private AssignmentService assignmentService;
	
	// 과제 등록
    @Operation(summary = "과제 등록")
    @ApiResponse(responseCode = "200", description = "과제 등록 성공")
    @PostMapping(value = "/" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public int insert(
            @RequestPart("assignment") AssignmentDto assignmentDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @CurrentUser TokenParseResponseVO parseVO
            ) throws IllegalStateException, IOException {

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        return assignmentService.insert(
                assignmentDto,
                files,
                parseVO.getNoType(), // 현재 로그인 직원번호
                tutor
        );
    }
    
    // 특정 강의의 최근 5개 과제 목록 조회
    @Operation(summary = "특정 강의의 최근 5개 과제 목록 조회")
    @GetMapping("/course/{courseNo}/recent")
    public List<AssignmentListVO> selectRecentListByCourse(
            @PathVariable int courseNo) {

        return assignmentService.selectRecentListByCourse(courseNo);
    }
    
    //직원 페이지네이션 + 검색 + 과제목록
    @Operation(summary = "직원 페이지네이션 + 검색 + 과제목록 조회")
    @GetMapping
    public PageResponseVO<AssignmentListVO> selectManageList(
    		 @ModelAttribute AssignmentSearchVO search,
    	     @CurrentUser TokenParseResponseVO parseVO){
    	boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());
    	
    	return assignmentService.selectManageList(
    			search,
    			parseVO.getNoType(),
    			tutor
    	);
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
        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

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
    	
        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

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

        boolean tutor = parseVO.getRoleNames().contains(RoleType.TUTOR.getCode());

        assignmentService.deleteFile(
                assignmentNo,
                attachNo,
                parseVO.getNoType(),
                tutor
        );
    }
    
    // 과제 마감
    @Operation(summary = "과제 마감")
    @PutMapping("/{assignmentNo}/close")
    public boolean close(
            @PathVariable int assignmentNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        boolean tutor =
                parseVO.getRoleNames()
                        .contains(RoleType.TUTOR.getCode());

        return assignmentService.close(
                assignmentNo,
                parseVO.getNoType(),
                tutor
        );
    }
}
