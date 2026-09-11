package com.kh.khedu.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kh.khedu.dao.ScoreDao;
import com.kh.khedu.dto.ScoreDto;

@Service
public class ScoreService {

    @Autowired
    private ScoreDao scoreDao;

    public void insert(ScoreDto scoreDto) {
        scoreDao.insert(scoreDto);
    }

    public ScoreDto getScore(int scoreNo) {
        return scoreDao.selectOne(scoreNo);
    }

    public List<ScoreDto> getScoresByStudent(int studentNo) {
        return scoreDao.selectListByStudent(studentNo);
    }

    public boolean update(ScoreDto scoreDto) {
        return scoreDao.update(scoreDto);
    }

    public boolean delete(int scoreNo) {
        return scoreDao.delete(scoreNo);
    }
}