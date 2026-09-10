package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;

@Repository
public class CourseDaoMybatis implements CourseDao {
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<CourseDto> selectTeachingListByEmployee(int employeeNo) {
		 return sqlSession.selectList("mapper.course.selectTeachingListByEmployee", employeeNo);
	}
	
	//강좌 번호생성
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.course.sequence");
	}
	
	//강좌 등록
	@Override
	public void insertCourse(CourseDto courseDto) {
		sqlSession.insert("mapper.course.add", courseDto);
	}
	
	//강좌 목록
	@Override
	public List<CourseListVO> selectCourseList() {
		return sqlSession.selectList("mapper.course.list");
	}

	//강좌 상세
	@Override
	public CourseDetailVO selectCourseDetail(int courseNo) {
		return sqlSession.selectOne("mapper.course.detail", courseNo);
	}

	@Override
	public List<CourseListVO> selectSearchList(CourseSearchVO search) {
		return sqlSession.selectList("mapper.course.searchList", search);
	}
	
	@Override
	public int selectCount(CourseSearchVO search) {
		return sqlSession.selectOne("mapper.course.selectCount", search);
	}
	
	//course_status를 모집중 -> 진행중으로 변경
	@Override
	public boolean updateStatus(int courseNo, String status) {
		Map<String, Object> param = new HashMap<>();
		param.put("courseNo", courseNo);
		param.put("status", status);
		return sqlSession.update("mapper.course.updateStatus", param) > 0;
	}
	
	//강좌번호로 강좌조회
	@Override
	public CourseDto selectOneByCourseNo(int courseNo) {
		return sqlSession.selectOne("mapper.course.selectOneByCourseNo", courseNo);
	}

	//shceuldNo로 강좌조회
	@Override
	public CourseDto selectOneByScheduleNo(int scheduleNo) {
		return sqlSession.selectOne("mapper.course.selectOneByScheduleNo", scheduleNo);
	}

}
