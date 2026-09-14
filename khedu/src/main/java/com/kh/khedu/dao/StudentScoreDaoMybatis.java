package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.score.ScoreListResponseVO;
import com.kh.khedu.vo.score.StudentExamVO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudentScoreDaoMybatis implements StudentScoreDao {

    private final SqlSession sqlSession;
    private final String NAMESPACE = "scoreMapper."; // 실제 매퍼 네임스페이스에 맞게 수정하세요!

    @Override
    public List<ScoreListResponseVO> selectSchoolExamScoreList(int studentNo, String scoreName) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentNo", studentNo);
        params.put("scoreName", scoreName); // ex: '1학기 중간고사'
        return sqlSession.selectList(NAMESPACE + "selectSchoolExamScoreList", params);
    }

    @Override
    public List<ScoreListResponseVO> selectMockExamScoreList(int studentNo, String scoreName) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentNo", studentNo);
        params.put("scoreName", scoreName); // ex: '3월 모의고사'
        return sqlSession.selectList(NAMESPACE + "selectMockExamScoreList", params);
    }
    
    @Override
    public List<StudentExamVO> selectStudentExamList(int studentNo) {
        return sqlSession.selectList(NAMESPACE + "selectStudentExamList", studentNo);
    }
}
