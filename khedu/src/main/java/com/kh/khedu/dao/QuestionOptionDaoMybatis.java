package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.QuestionOptionDto;

@Repository
public class QuestionOptionDaoMybatis implements QuestionOptionDao {

    @Autowired
    private SqlSession sqlSession;

    // 보기 번호 시퀀스 생성
    @Override
    public int sequence() {
        return sqlSession.selectOne("mapper.questionOption.sequence");
    }

    // 보기 등록
    @Override
    public void insert(QuestionOptionDto questionOptionDto) {
        sqlSession.insert("mapper.questionOption.insert", questionOptionDto);
    }

    // 보기 번호로 단일 보기 조회
    @Override
    public QuestionOptionDto selectOne(int optionNo) {
        return sqlSession.selectOne("mapper.questionOption.selectOne", optionNo);
    }

    // 특정 문제의 보기 목록 조회
    @Override
    public List<QuestionOptionDto> selectListByQuestion(int questionNo) {
        return sqlSession.selectList("mapper.questionOption.selectListByQuestion", questionNo);
    }

    // 보기 수정
    @Override
    public boolean update(QuestionOptionDto questionOptionDto) {

        return sqlSession.update("mapper.questionOption.update", questionOptionDto) > 0;
    }

    // 보기 삭제
    @Override
    public boolean delete(int optionNo) {
        return sqlSession.delete("mapper.questionOption.delete",optionNo) > 0;
    }

    // 특정 문제의 보기 전체 삭제
    @Override
    public boolean deleteByQuestion(int questionNo) {
        return sqlSession.delete("mapper.questionOption.deleteByQuestion", questionNo) > 0;
    }

    @Override
    public int countByQuestionOrder(int questionNo, int optionOrder) {
        Map<String, Object> params = Map.of(
                "questionNo", questionNo,
                "optionOrder", optionOrder
        );

        return sqlSession.selectOne("mapper.questionOption.countByQuestionOrder",params);
    }

    @Override
    public int countByQuestionOrderExcludeSelf(int questionNo, int optionOrder, int optionNo) {

        Map<String, Object> params = Map.of(
                "questionNo", questionNo,
                "optionOrder", optionOrder,
                "optionNo", optionNo
        );

        return sqlSession.selectOne("mapper.questionOption.countByQuestionOrderExcludeSelf",params);
    }
}