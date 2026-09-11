package com.kh.khedu.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kh.khedu.dto.ScoreDto;
import com.kh.khedu.service.ScoreService;

@RestController
@RequestMapping("/api/score")
public class ScoreRestController {

    @Autowired
    private ScoreService scoreService;

    // 성적 등록 (POST)
    @PostMapping("/add")
    public ResponseEntity<String> insert(@RequestBody ScoreDto scoreDto) {
        scoreService.insert(scoreDto);
        return ResponseEntity.ok("성적이 성공적으로 등록되었습니다.");
    }

    // 특정 학생의 성적 목록 조회 (GET)
    @GetMapping("/list/{studentNo}")
    public ResponseEntity<List<ScoreDto>> listByStudent(@PathVariable int studentNo) {
        List<ScoreDto> list = scoreService.getScoresByStudent(studentNo);
        return ResponseEntity.ok(list);
    }

    // 성적 수정 (PUT)
    @PutMapping("/edit")
    public ResponseEntity<String> update(@RequestBody ScoreDto scoreDto) {
        boolean result = scoreService.update(scoreDto);
        if (result) {
            return ResponseEntity.ok("성적이 수정되었습니다.");
        }
        return ResponseEntity.badRequest().body("성적 수정에 실패했습니다.");
    }

    // 성적 삭제 (DELETE)
    @DeleteMapping("/delete/{scoreNo}")
    public ResponseEntity<String> delete(@PathVariable int scoreNo) {
        boolean result = scoreService.delete(scoreNo);
        if (result) {
            return ResponseEntity.ok("성적이 삭제되었습니다.");
        }
        return ResponseEntity.badRequest().body("성적 삭제에 실패했습니다.");
    }
}