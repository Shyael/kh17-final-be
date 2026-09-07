package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AttemptAnswerDto;

@Repository
public class AttemptAnswerDaoMybatis
        implements AttemptAnswerDao {

    @Autowired
    private SqlSession sqlSession;

    // 답안 등록
    @Override
    public void insert(AttemptAnswerDto attemptAnswerDto) {
        sqlSession.insert("mapper.attemptAnswer.insert", attemptAnswerDto);
    }

    // 특정 응시의 특정 문제 답안 조회
    @Override
    public AttemptAnswerDto selectOne(int attemptNo, int questionNo) {
        Map<String, Object> params = Map.of("attemptNo", attemptNo, "questionNo", questionNo);
        return sqlSession.selectOne("mapper.attemptAnswer.selectOne", params);
    }

    // 특정 응시의 전체 답안 목록 조회
    @Override
    public List<AttemptAnswerDto> selectListByAttempt(int attemptNo) {
        return sqlSession.selectList("mapper.attemptAnswer.selectListByAttempt", attemptNo);
    }

    // 답안 수정
    @Override
    public boolean update(AttemptAnswerDto attemptAnswerDto) {
        return sqlSession.update("mapper.attemptAnswer.update", attemptAnswerDto) > 0;
    }

    // 특정 답안 삭제
    @Override
    public boolean delete(int attemptNo, int questionNo) {
        Map<String, Object> params = Map.of("attemptNo", attemptNo, "questionNo", questionNo);
        return sqlSession.delete("mapper.attemptAnswer.delete", params) > 0;
    }

    // 특정 응시의 전체 답안 삭제
    @Override
    public boolean deleteByAttempt(int attemptNo) {
        return sqlSession.delete("mapper.attemptAnswer.deleteByAttempt", attemptNo) > 0;
    }
}