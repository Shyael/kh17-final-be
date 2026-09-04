package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;

@Repository
public class CourseDaoMybatis implements CourseDao {
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<CourseDto> selectTeachingListByEmployee(int employeeNo) {
		 return sqlSession.selectList("mapper.course.selectTeachingListByEmployee", employeeNo);
	}
	
	//강좌 등록
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.course.sequence");
	}
	
	@Override
	public void insertCourse(CourseCreateRequestVO request) {
		sqlSession.insert("mapper.course.add", request);
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


}
