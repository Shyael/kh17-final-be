package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;
import com.kh.khedu.vo.course.StudentCourseListVO;

@Repository
public class CourseDaoMybatis implements CourseDao {
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<CourseDto> selectTeachingListByEmployee(int employeeNo) {
		 return sqlSession.selectList("mapper.course.selectTeachingListByEmployee", employeeNo);
	}
	
	@Override
	public List<StudentCourseListVO> selectListByStudent(int studentNo) {
		return sqlSession.selectList("mapper.course.selectListByStudent", studentNo);
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

	

}
