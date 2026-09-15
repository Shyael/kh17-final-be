package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.ScoreDto;
import com.kh.khedu.vo.score.ScoreListResponseVO;
import com.kh.khedu.vo.score.StudentExamVO;

public interface StudentScoreService {
    // 성적 타입(내신/모의고사)을 함께 받아서 알맞은 성적 리스트를 반환
    List<ScoreListResponseVO> getScoreList(int studentNo, String scoreName, String scoreType);
    List<StudentExamVO> getStudentExamList(int studentNo);
    Integer selectStudentNoByAccountNo(int accountNo);
    List<ScoreDto> getScoreHistory(int studentNo);
}