package com.kh.khedu.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.khedu.dto.ScoreDto;

@Repository
public class ScoreDaoMybatis implements ScoreDao {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public void insert(ScoreDto scoreDto) {
        sqlSession.insert("mapper.score.insert", scoreDto);
    }

    @Override
    public ScoreDto selectOne(int scoreNo) {
        return sqlSession.selectOne("mapper.score.selectOne", scoreNo);
    }

    @Override
    public List<ScoreDto> selectListByStudent(int studentNo) {
        return sqlSession.selectList("mapper.score.selectListByStudent", studentNo);
    }

    @Override
    public boolean update(ScoreDto scoreDto) {
        return sqlSession.update("mapper.score.update", scoreDto) > 0;
    }

    @Override
    public boolean delete(int scoreNo) {
        return sqlSession.delete("mapper.score.delete", scoreNo) > 0;
    }
}