package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dto.StudentCourseDto;
import com.kh.khedu.service.StudentCourseService;
import com.kh.khedu.service.StudentService;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.StudentDetailResponseVO;
import com.kh.khedu.vo.student.StudentListResponseVO;
import com.kh.khedu.vo.student.StudentUpdateRequestVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학생 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee/student")
public class StudentRestController {
	@Autowired 
	private StudentService studentService;
	@Autowired
	private StudentCourseService studentCourseService;
	
    //학생 목록 전체 조회
	@GetMapping("/list")
    public ResponseEntity<List<StudentListResponseVO>> getStudentList(
            @RequestParam(required = false, defaultValue = "전체") String filter,
            @RequestParam(required = false, defaultValue = "") String searchKeyword) {
        
        List<StudentListResponseVO> list = studentService.getStudentList(filter, searchKeyword);
        return ResponseEntity.ok(list);
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
    
	// 학생 재원 승인 API
    @PatchMapping("/approve/{studentNo}")
    public ResponseEntity<String> approveStudent(@PathVariable int studentNo) {
        try {
            studentService.approveStudent(studentNo);
            return ResponseEntity.ok("재원 처리(승인)가 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("승인 처리 실패");
        }
    }
    
    @PostMapping("/course/add")
    public ResponseEntity<String> addCourse(@RequestBody StudentCourseDto dto) {
        String result = studentCourseService.enrollCourse(dto);
        
        if ("GRADE_MISMATCH".equals(result)) {
            return ResponseEntity.badRequest().body("신청 불가: 학생의 학년과 강의 대상 학년이 일치하지 않습니다.");
        } else if ("TIME_CONFLICT".equals(result)) {
            return ResponseEntity.badRequest().body("신청 불가: 기존에 수강 중인 강의와 요일/시간이 겹칩니다.");
        }
        
        return ResponseEntity.ok("성공적으로 수강 신청되었습니다.");
    }

}
