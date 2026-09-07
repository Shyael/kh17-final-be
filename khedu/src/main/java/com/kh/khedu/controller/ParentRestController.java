package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.service.ParentService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.account.CheckPasswordRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.parent.ChangeParentRequestVO;
import com.kh.khedu.vo.parent.ChangeParentResponseVO;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parent.ParentJoinRequestVO;
import com.kh.khedu.vo.parentStudent.ParentStudentRelatioshipUpdateRequestVO;
import com.kh.khedu.vo.studentLink.ParentLinkRequestVO;
import com.kh.khedu.vo.studentLink.ParentLinkResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
	
}
