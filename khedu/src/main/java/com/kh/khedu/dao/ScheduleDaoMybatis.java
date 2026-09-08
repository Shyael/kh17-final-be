package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

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
	
	// 강사 기존 수업과 충돌 확인
	@Override
	public int checkTutorScheduleConflict(int employeeNo, ScheduleCreateRequestVO request) {
		Map<String, Object> param = new HashMap<>();
		
		param.put("employeeNo", employeeNo);
		param.put("request", request);
		
		return sqlSession.selectOne("mapper.schedule.checkTutorScheduleConfilct", param);
	}
	
	//강의실 기존 수업과 충돌 확인
	@Override
	public int checkClassroomScheduleConflict(int classroomNo, ScheduleCreateRequestVO request) {
		Map<String, Object> param = new HashMap<>();
		param.put("classroomNo", classroomNo);
		param.put("request", request);
		return sqlSession.selectOne("mapper.classroom.checkClassroomScheduleConflict", param);
	}

}
