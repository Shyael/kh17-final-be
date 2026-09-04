package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.configuration.EmailConfiguration;

import javax.crypto.SecretKey;
import com.kh.khedu.dto.AcademyDto;

@Repository
public class AcademyDaoMybatis implements AcademyDao {

    private final SecretKey secretKey;

    private final EmailConfiguration emailConfiguration;

	@Autowired
	private SqlSession sqlSession;

    AcademyDaoMybatis(EmailConfiguration emailConfiguration, SecretKey secretKey) {
        this.emailConfiguration = emailConfiguration;
        this.secretKey = secretKey;
    }

	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.academy.sequence");
	}

	@Override
	public void insert(AcademyDto academyDto) {
		sqlSession.insert("mapper.academy.insert", academyDto);
	}

	@Override
	public AcademyDto selectOne() {
		return sqlSession.selectOne("mapper.academy.selectOne");
	}

	@Override
	public boolean update(AcademyDto academyDto) {
		return sqlSession.update("mapper.academy.update", academyDto) > 0;
	}
	

	@Override
	public boolean delete(int academyNo) {
		return sqlSession.delete("mapper.academy.delete", academyNo) > 0;
	}

	@Override
	public void connect(int academyNo, int attachNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("academyNo", academyNo);
		params.put("attachNo", attachNo);
		sqlSession.insert("mapper.academy.connect", params);
		
	}

	@Override
	public List<Integer> selectDetailImages(int academyNo) {
		return sqlSession.selectList("mapper.academy.selectDetailImages",academyNo);
	}
	
}
