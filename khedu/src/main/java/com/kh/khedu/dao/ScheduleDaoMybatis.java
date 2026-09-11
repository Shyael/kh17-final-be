package com.kh.khedu.dao;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
		param.put("schedule", request);
		
		return sqlSession.selectOne("mapper.schedule.checkTutorScheduleConflict", param);
	}
	
	//강의실 기존 수업과 충돌 확인
	@Override
	public int checkClassroomScheduleConflict(int classroomNo, ScheduleCreateRequestVO request) {
		Map<String, Object> param = new HashMap<>();
		param.put("classroomNo", classroomNo);
		param.put("request", request);
		return sqlSession.selectOne("mapper.schedule.checkClassroomScheduleConflict", param);
	}
	
	//스케줄 no로 스케줄 정보 확인
	@Override
	public ScheduleDto selectOneByScheduleNo(int scheduleNo) {
		return sqlSession.selectOne("mapper.schedule.selectOneByScheduleNo", scheduleNo);
	}
	
	//schedule_open의 날짜를 오늘로 변경
	@Override
	public boolean updateOpenByCourseNo(int scheduleNo, LocalDate today) {
		Map<String, Object> param = new HashMap<>();
		param.put("scheduleNo", scheduleNo);
		param.put("today", today);
		return sqlSession.update("mapper.schedule.updateOpen", param) > 0;
	}
	@Override
	public List<ScheduleDto> selectActiveSchedules() {
		return sqlSession.selectList("mapper.schedule.selectActiveSchedules");
	}
	
	//강좌번호로 해당하는 스케줄목록 불러오기
	@Override
	public List<ScheduleDto> selectListByCourseNo(int courseNo) {
		return sqlSession.selectList("mapper.schedule.selectListByCourseNo", courseNo);
	}
	
	// 개강일(schedule_open)이 도달했거나 null이 아닌 오늘 스케줄 조회
	@Override
	public List<ScheduleDto> selectTodayActiveSchedules(String todayKorean) {
		return sqlSession.selectList("mapper.schedule.selectTodayActiveSchedules", todayKorean);
	}

}
