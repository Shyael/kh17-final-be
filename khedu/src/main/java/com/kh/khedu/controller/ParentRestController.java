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
@RequestMapping("/api/parent")
public class ParentRestController {
	
	@Autowired
	private ParentService parentService;
	
	//학부모 등록
	@ApiResponse(responseCode = "200", description="등록 성공")
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountJoinResponseVO join(
			@RequestBody ParentJoinRequestVO request) {
			//회원가입 처리
			AccountJoinResponseVO accountJoinResponseVO 
			 = parentService.joinParent(request);
		return accountJoinResponseVO;
	}
	
	//내 정보라는 건  cookie에 포함된 loginId를 읽으면 된다
	//stateless(무상태) 서버의 세션 대체 방안
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping(value = "/me", produces= "application/json")
	public ParentDetailVO me(
		@CurrentUser TokenParseResponseVO parseVO
	) {
		ParentDetailVO parentDetailVO = parentService.findMyInfo(parseVO.getAccountId());
		return parentDetailVO; 
	}
	
	//개인정보 수정(본인)
	@PutMapping("/")
	public ChangeParentResponseVO updateAll(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody ChangeParentRequestVO request
	) {
		return parentService.updateMyInfo(request, parseVO);
	}
	
	//비밀번호 확인
	@PostMapping("/password-check")
	public boolean checkPassword(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody CheckPasswordRequestVO request
	) {
		return parentService.checkPassword(request, parseVO);
	}
	
	//연동코드
	@PostMapping("/link-student")
	public ResponseEntity<ParentLinkResponseVO> link(
			@CurrentUser TokenParseResponseVO parseVO,
			@Valid @RequestBody ParentLinkRequestVO request
	) {
		ParentLinkResponseVO response = parentService.linkStudent(request, parseVO);
		return ResponseEntity.ok(response);
	}
	
	//관계 수정
	@PutMapping("/relationship")
	public void relationship(
			@RequestBody ParentStudentRelatioshipUpdateRequestVO request,
			@CurrentUser TokenParseResponseVO parseVO) {
		parentService.updateRelationship(parseVO, request);
	}
	
	@GetMapping("/api/parent/student/{studentNo}")
    public ResponseEntity<ParentDetailVO> getParentInfoByStudent(@PathVariable int studentNo) {
        // Service -> DAO를 거쳐 방금 만든 쿼리를 실행합니다.
        ParentDetailVO parentInfo = parentService.findParentDetailByStudentNo(studentNo);
        
        return ResponseEntity.ok(parentInfo);
    }
	
	// 관리자용 학부모 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<ParentDetailVO>> searchParents(@RequestParam String keyword) {
        List<ParentDetailVO> list = parentService.searchParents(keyword);
        return ResponseEntity.ok(list);
    }
	
}
