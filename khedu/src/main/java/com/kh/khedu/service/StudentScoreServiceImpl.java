package com.kh.khedu.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.khedu.dao.StudentScoreDao;
import com.kh.khedu.vo.score.ScoreListResponseVO;
import com.kh.khedu.vo.score.StudentExamVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentScoreServiceImpl implements StudentScoreService {

    private final StudentScoreDao studentScoreDao;

    @Override
    public List<ScoreListResponseVO> getScoreList(int studentNo, String scoreName, String scoreType) {
        
        // 성적 타입에 따라 알맞은 DAO(쿼리) 호출
        if ("내신".equals(scoreType)) {
            return studentScoreDao.selectSchoolExamScoreList(studentNo, scoreName);
        } 
        else if ("모의고사".equals(scoreType)) {
            return studentScoreDao.selectMockExamScoreList(studentNo, scoreName);
        }
        else if ("수능".equals(scoreType)) {
            return studentScoreDao.selectMockExamScoreList(studentNo, scoreName);
        }
        
        // 예외 처리: 타입이 안 맞으면 빈 배열 반환 (에러 방지)
        return Collections.emptyList(); 
    }
    
    @Override
    public List<StudentExamVO> getStudentExamList(int studentNo) {
        return studentScoreDao.selectStudentExamList(studentNo);
    }
}
