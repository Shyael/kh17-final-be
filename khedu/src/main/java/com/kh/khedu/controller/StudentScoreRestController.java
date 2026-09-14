package com.kh.khedu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.StudentScoreService;
import com.kh.khedu.vo.score.ScoreListResponseVO;
import com.kh.khedu.vo.score.StudentExamVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/academy/score")
@RequiredArgsConstructor
public class StudentScoreRestController {

    private final StudentScoreService studentScoreService;

    // 성적 리스트 및 등락 조회 API
    // GET /api/academy/score/student/list?studentNo=1&scoreName=3월 모의고사&scoreType=모의고사
    @GetMapping("/list")
    public ResponseEntity<List<ScoreListResponseVO>> getScoreListAndDiff(
            @RequestParam int studentNo,
            @RequestParam String scoreName,
            @RequestParam String scoreType) {
            
        // 서비스 호출 한 방으로 깔끔하게 정리!
        List<ScoreListResponseVO> resultList = studentScoreService.getScoreList(studentNo, scoreName, scoreType);
        
        return ResponseEntity.ok(resultList);
    }
    
 // 🌟 드롭다운용: 학생이 응시한 시험 목록 조회
    // GET /api/academy/score/student/exams?studentNo=1
    @GetMapping("/exams")
    public ResponseEntity<List<StudentExamVO>> getStudentExamList(@RequestParam int studentNo) {
        List<StudentExamVO> examList = studentScoreService.getStudentExamList(studentNo);
        return ResponseEntity.ok(examList);
    }
}