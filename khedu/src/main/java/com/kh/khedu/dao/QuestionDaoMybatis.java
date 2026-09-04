package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.QuestionDto;

@Repository
public class QuestionDaoMybatis implements QuestionDao {

    @Autowired
    private SqlSession sqlSession;

    // 문제 번호 시퀀스 생성
    @Override
    public int sequence() {
        return sqlSession.selectOne("mapper.question.sequence");
    }

    // 문제 등록
    @Override
    public void insert(QuestionDto questionDto) {
        sqlSession.insert("mapper.question.insert",questionDto);
    }

    // 문제 번호로 단일 문제 조회
    @Override
    public QuestionDto selectOne(int questionNo) {
        return sqlSession.selectOne("mapper.question.selectOne",questionNo);
    }

    // 특정 시험의 문제 목록 조회
    @Override
    public List<QuestionDto> selectListByExam(int examNo) {
        return sqlSession.selectList("mapper.question.selectListByExam",examNo);
    }

    // 문제 수정
    // 수정된 행이 1개 이상이면 true 반환
    @Override
    public boolean update(QuestionDto questionDto) {
        return sqlSession.update("mapper.question.update",questionDto) > 0;
    }

    // 문제 삭제
    // 삭제된 행이 1개 이상이면 true 반환
    @Override
    public boolean delete(int questionNo) {
        return sqlSession.delete("mapper.question.delete",questionNo) > 0;
    }

    // 문제와 첨부파일 연결
    @Override
    public void connect(int questionNo, int attachNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("questionNo", questionNo);
        params.put("attachNo", attachNo);
        sqlSession.insert("mapper.question.connect",params);
    }

    // 특정 문제의 첨부파일 번호 목록 조회
    @Override
    public List<Integer> selectFiles(int questionNo) {
        return sqlSession.selectList("mapper.question.selectFiles",questionNo);
    }
}