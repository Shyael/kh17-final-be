package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ScheduleDto;

@Repository
public class ScheduleDaoMybatis implements ScheduleDao {

	@Autowired
	private SqlSession sqlSession;
	
	//등록
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.schedule.sequence");
	}
	@Override
	public void insertSchedule(ScheduleDto scheduleDto) {
		sqlSession.insert("mapper.schedule.add", scheduleDto);
	}

}
