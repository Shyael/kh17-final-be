package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.vo.attendance.AttendanceStudentResponseVO;

@Repository
public class AttemptDaoMybatis implements AttemptDao {

    @Autowired
    private SqlSession sqlSession;

    // 응시 번호 시퀀스 생성
    @Override
    public int sequence() {
        return sqlSession.selectOne("mapper.attempt.sequence");
    }

    // 시험 응시 시작
    @Override
    public void insert(AttemptDto attemptDto) {
        sqlSession.insert("mapper.attempt.insert", attemptDto);
    }

    // 응시 번호로 단일 응시 조회
    @Override
    public AttemptDto selectOne(int attemptNo) {
        return sqlSession.selectOne("mapper.attempt.selectOne", attemptNo);
    }

    // 특정 학생의 특정 시험 응시 조회
    @Override
    public AttemptDto selectOneByExamStudent(int examNo, int studentNo) {
        Map<String, Object> params = Map.of("examNo", examNo,"studentNo", studentNo);
        return sqlSession.selectOne("mapper.attempt.selectOneByExamStudent",params);
    }

    // 시험 제출 처리
    @Override
    public boolean submit(AttemptDto attemptDto) {
        return sqlSession.update("mapper.attempt.submit", attemptDto) > 0;
    }
    
    @Override
    public String selectStudentName(int studentNo) {
        return sqlSession.selectOne("mapper.attempt.selectStudentName",studentNo);
    }
    
}