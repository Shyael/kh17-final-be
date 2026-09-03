package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dto.ParentStudentDto;
import com.kh.khedu.service.ParentService;
import com.kh.khedu.service.ParentStudentService;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parentStudent.StudentParentDetailVO; // 팀원분이 만드신 다중 조회 VO

@RestController
@RequestMapping("/api/parent-student")
public class ParentStudentRestController {

    @Autowired
    private ParentStudentService parentStudentService;
    
    @Autowired
    private ParentService parentService;

    // 1. 다이렉트 연동 API
    @PostMapping("/direct-link")
    public ResponseEntity<String> directLinkParentStudent(@RequestBody ParentStudentDto dto) {
        try {
            parentStudentService.directLink(dto);
            return ResponseEntity.ok("성공적으로 연동되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류로 연동에 실패했습니다.");
        }
    }
    
    // 2. 학부모 검색 API (🌟 주소 꼬임 해결: /search 만 작성)
    // 최종 주소: /api/parent-student/search
    @GetMapping("/search")
    public ResponseEntity<?> searchParents(@RequestParam String keyword) {
        List<ParentDetailVO> list = parentService.searchParents(keyword);
        return ResponseEntity.ok(list);
    }

    // 3. 특정 학생에 연결된 학부모 목록 조회 API (🌟 이거 없으면 화면에 부모님 리스트 안 뜸!)
    // 최종 주소: /api/parent-student/list/{studentNo}
    @GetMapping("/list/{studentNo}")
    public ResponseEntity<?> getParentList(@PathVariable int studentNo) {
        // 팀원분이 만드신 다중 조회 쿼리(selectParentListByStudentNo)를 실행하도록 서비스 호출
        List<StudentParentDetailVO> list = parentStudentService.selectParentListByStudentNo(studentNo);
        return ResponseEntity.ok(list);
    }
    
    @DeleteMapping("/remove-link")
    public ResponseEntity<String> removeLinkParentStudent(
            @RequestParam int parentNo, 
            @RequestParam int studentNo) {
        try {
            // 파라미터로 받은 번호 2개를 DTO로 포장해서 서비스로 넘김
            ParentStudentDto dto = ParentStudentDto.builder()
                    .parentNo(parentNo)
                    .studentNo(studentNo)
                    .build();
            
            parentStudentService.removeLink(dto);
            return ResponseEntity.ok("연동이 성공적으로 해제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("해제 실패");
        }
    }
}