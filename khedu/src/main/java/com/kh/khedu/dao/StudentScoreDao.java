package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ScoreDto;
import com.kh.khedu.vo.score.ScoreListResponseVO;
import com.kh.khedu.vo.score.StudentExamVO;

public interface StudentScoreDao {
    // 1. 내신 성적 리스트 조회 (하드코딩 정렬)
    List<ScoreListResponseVO> selectSchoolExamScoreList(int studentNo, String scoreName);
    
    // 2. 모의고사(수능) 성적 리스트 조회 (과목 코드 정렬)
    List<ScoreListResponseVO> selectMockExamScoreList(int studentNo, String scoreName);
    
    List<StudentExamVO> selectStudentExamList(int studentNo);
    Integer selectStudentNoByAccountNo(int accountNo);
    List<ScoreDto> selectListByStudent(int studentNo);
}