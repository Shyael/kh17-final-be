package com.kh.khedu.dao;

import java.util.List;
import com.kh.khedu.dto.ScoreDto;

public interface ScoreDao {
    void insert(ScoreDto scoreDto);
    ScoreDto selectOne(int scoreNo);
    List<ScoreDto> selectListByStudent(int studentNo);
    boolean update(ScoreDto scoreDto);
    boolean delete(int scoreNo);
}