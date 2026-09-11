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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dto.AssignmentSubmitDto;
import com.kh.khedu.service.AssignmentSubmitService;
import com.kh.khedu.vo.assignment.AssignmentSubmitDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitListVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitStudentListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "과제 제출 관리(학생/학부모)")
@RestController
@RequestMapping("/api/academy/assignment-submit")
public class AcademyAssignmentSubmitRestController {

    @Autowired
    private AssignmentSubmitService assignmentSubmitService;


    // 과제 제출
    @Operation(summary = "과제 제출")
    @ApiResponse(responseCode = "200", description = "과제 제출 성공")
    @PostMapping(
		value = "/",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AssignmentSubmitDto insert(
    		  @RequestPart("submit")
              AssignmentSubmitDto assignmentSubmitDto,

              @RequestPart(value = "files", required = false)
              List<MultipartFile> files,

              @CurrentUser
              TokenParseResponseVO parseVO
      ) throws IllegalStateException, IOException {

    	//로그인한 학생번호 설정
        assignmentSubmitDto.setStudentNo(parseVO.getNoType());

        assignmentSubmitService.insert(assignmentSubmitDto, files);

        return assignmentSubmitDto;
    }

    // 학생용 : 특정 과제에 대한 내 제출 조회
    @Operation(summary = "특정 과제 내 제출 조회")
    @GetMapping("/assignment/{assignmentNo}/me")
    public AssignmentSubmitDetailVO selectOneByAssignmentStudent(
            @PathVariable int assignmentNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        AssignmentSubmitDto assignmentSubmitDto =
                AssignmentSubmitDto.builder()
                    .assignmentNo(assignmentNo)
                    .studentNo(parseVO.getNoType())
                    .build();

        return assignmentSubmitService
                .selectOneByAssignmentStudent(assignmentSubmitDto);
    }
    
    // 학생용 : 제출 내용 수정
    @Operation(summary = "과제 제출 내용 수정")
    @ApiResponse(responseCode = "200", description = "과제 제출 수정 성공")
    @PutMapping(
	    value = "/{submitNo}",
	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public boolean update(
    		@PathVariable int submitNo,

            @RequestPart("submit")
            AssignmentSubmitDto assignmentSubmitDto,

            @RequestPart(value = "files", required = false)
            List<MultipartFile> files,

            @CurrentUser
            TokenParseResponseVO parseVO
    ) throws IllegalStateException, IOException {

        assignmentSubmitDto.setSubmitNo(submitNo);

        return assignmentSubmitService.update(
                assignmentSubmitDto,
                files,
                parseVO.getNoType()
        );
    }

    // 학생용 : 과제 제출 삭제
    @Operation(summary = "과제 제출 삭제")
    @ApiResponse(responseCode = "200", description = "과제 제출 삭제 성공")
    @DeleteMapping("/{submitNo}")
    public boolean delete(
            @PathVariable int submitNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        return assignmentSubmitService.delete(
                submitNo,
                parseVO.getNoType()
        );
    }

    // 학생용 : 제출 첨부파일 삭제
    @Operation(summary = "과제 제출 첨부파일 삭제")
    @ApiResponse(responseCode = "200", description = "과제 제출 첨부파일 삭제 성공")
    @DeleteMapping("/{submitNo}/file/{attachNo}")
    public void deleteFile(
            @PathVariable int submitNo,
            @PathVariable int attachNo,
            @CurrentUser TokenParseResponseVO parseVO) {

        assignmentSubmitService.deleteFile(
                submitNo,
                attachNo,
                parseVO.getNoType()
        );
    }
    
    //학부모용 : 자녀 과제 제출내용 조회
    @GetMapping("/parent/student/{studentNo}/assignment/{assignmentNo}")
    public AssignmentSubmitDetailVO selectOneByParentStudent(
    		@PathVariable int studentNo,
    		@PathVariable int assignmentNo,
    		@CurrentUser TokenParseResponseVO parseVO) {
    	return assignmentSubmitService.selectOneByParentStudent(
    			parseVO.getNoType(),//parentNO
    			studentNo,
    			assignmentNo
    	);
    }

}