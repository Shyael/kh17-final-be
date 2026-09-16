package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ScoreDto;
import com.kh.khedu.vo.score.ScoreListResponseVO;
import com.kh.khedu.vo.score.StudentExamVO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudentScoreDaoMybatis implements StudentScoreDao {

    private final SqlSession sqlSession;

    @Override
    public List<ScoreListResponseVO> selectSchoolExamScoreList(int studentNo, String scoreName) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentNo", studentNo);
        params.put("scoreName", scoreName); // ex: '1학기 중간고사'
        return sqlSession.selectList("mapper.score.selectSchoolExamScoreList", params);
    }

    @Override
    public List<ScoreListResponseVO> selectMockExamScoreList(int studentNo, String scoreName) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentNo", studentNo);
        params.put("scoreName", scoreName); // ex: '3월 모의고사'
        return sqlSession.selectList("mapper.score.selectMockExamScoreList", params);
    }
    
    @Override
    public List<StudentExamVO> selectStudentExamList(int studentNo) {
        return sqlSession.selectList("mapper.score.selectStudentExamList", studentNo);
    }
    
    public Integer selectStudentNoByAccountNo(int accountNo) {
        return sqlSession.selectOne("mapper.score.selectStudentNoByAccountNo", accountNo);
    }
    
    @Override
    public List<ScoreDto> selectListByStudent(int studentNo) {
        return sqlSession.selectList("mapper.score.selectListByStudent", studentNo);
    }
}
