package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.StudentService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.StudentDetailResponseVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO;
import com.kh.khedu.vo.student.StudentListResponseVO;
import com.kh.khedu.vo.student.StudentUpdateRequestVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학생 정보 관리 서비스")
@RestController
@RequestMapping("/api/student")
public class StudentRestController {
	@Autowired 
	private StudentService studentService;
	
	//학생 회원가입
	@ApiResponse(responseCode = "200", description = "등록 성공")
	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountJoinResponseVO join(
			@RequestBody StudentJoinRequestVO request) {
		//회원가입 처리
		AccountJoinResponseVO accountJoinResponseVO = studentService.joinStudent(request);
		
		return accountJoinResponseVO;
		
	}

    //학생 목록 전체 조회
    @GetMapping(value = "/list", produces = "application/json")
    public List<StudentListResponseVO> list() {
        return studentService.getStudentList();
    }
    
    //학생 상세 조회
    @Operation(summary = "학생 상세 조회", description = "특정 학생의 상세 정보를 반환합니다.")
    @GetMapping(value = "/detail/{studentNo}", produces = "application/json")
    public StudentDetailResponseVO detail(@PathVariable("studentNo") int studentNo) {
        return studentService.getStudentDetail(studentNo);
    }
    
    @Operation(summary = "학생 정보 수정", description = "기존 학생의 정보를 변경합니다.")
    @PutMapping(value = "/edit", consumes = "application/json")
    public ResponseEntity<String> edit(@RequestBody StudentUpdateRequestVO requestVO) {
        //서비스 호출하여 DB 데이터 수정
        studentService.updateStudentInfo(requestVO);
        //성공적으로 수정되었음을 프론트엔드에 알림
        return ResponseEntity.ok("학생 정보가 성공적으로 수정되었습니다.");
    }
    
    // 1. 이 학생이 받고 있는 할인 목록 보기
    @GetMapping("/{studentNo}/discount")
    public List<StudentDiscountVO> getStudentDiscounts(@PathVariable int studentNo) {
        return studentService.getStudentDiscounts(studentNo);
    }

    // 2. 이 학생에게 할인 혜택 추가하기
    @PostMapping("/{studentNo}/discount/{discountNo}")
    public ResponseEntity<String> applyDiscountToStudent(
            @PathVariable int studentNo, 
            @PathVariable int discountNo) {
            
        studentService.addStudentDiscount(studentNo, discountNo);
        return ResponseEntity.ok("학생에게 할인이 성공적으로 적용되었습니다.");
    }

    // 3. 이 학생의 할인 혜택 빼기
    @DeleteMapping("/discount/{studentDiscountNo}")
    public ResponseEntity<String> removeStudentDiscount(@PathVariable int studentDiscountNo) {
        studentService.removeStudentDiscount(studentDiscountNo);
        return ResponseEntity.ok("학생의 할인이 해제되었습니다.");
    }
	
}
