package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.ParentService;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parent.ParentSearchVO;
import com.kh.khedu.vo.parent.ParentUpdateRequestVO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학부모 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee/parent")
public class ParentRestController {
	
	@Autowired
	private ParentService parentService;
	
	@GetMapping("/student/{studentNo}")
    public ResponseEntity<List<ParentDetailVO>> getParentInfoByStudent(@PathVariable int studentNo) {
        List<ParentDetailVO> parentInfoList = parentService.findParentDetailByStudentNo(studentNo);
        return ResponseEntity.ok(parentInfoList);
    }
	
	// 관리자용 학부모 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<ParentDetailVO>> searchParents(@RequestParam String keyword) {
        List<ParentDetailVO> list = parentService.searchParents(keyword);
        return ResponseEntity.ok(list);
    }
    
    // 관리자 - 목록
    @GetMapping("/list")
    public ResponseEntity<PageResponseVO<ParentDetailVO>> getParentList(
    		@ModelAttribute ParentSearchVO searchVO,
    		@CurrentUser TokenParseResponseVO parseVO
    ){
    	PageResponseVO<ParentDetailVO> pageData = parentService.getParentList(searchVO, parseVO);
    	return ResponseEntity.ok(pageData);
    }
    
    @PatchMapping("/approve/{parentNo}")
    public ResponseEntity<String> approveParent(
            @PathVariable int parentNo,
            @CurrentUser TokenParseResponseVO parseVO
    ) {
        parentService.approveParent(parentNo, parseVO);
        return ResponseEntity.ok("학부모 가입 승인이 완료되었습니다.");
    }
    
 // 학부모 단건 상세 조회: GET /employee/parent/detail/{parentNo}
    @GetMapping("/detail/{parentNo}")
    public ResponseEntity<ParentDetailVO> getParentDetail(
            @PathVariable int parentNo,
            @CurrentUser TokenParseResponseVO parseVO
    ) {
        ParentDetailVO detail = parentService.getParentDetail(parentNo, parseVO);
        return ResponseEntity.ok(detail);
    }

    // 학부모 정보 수정: PUT /employee/parent/update
    @PutMapping("/update")
    public ResponseEntity<String> updateParentInfo(
            @RequestBody ParentUpdateRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO
    ) {
        parentService.updateParentInfo(requestVO, parseVO);
        return ResponseEntity.ok("학부모 정보가 성공적으로 수정되었습니다.");
    }
	
}
