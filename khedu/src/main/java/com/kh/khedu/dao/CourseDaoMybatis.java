package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.CourseDto;

@Repository
public class CourseDaoMybatis implements CourseDao {
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<CourseDto> selectTeachingListByEmployee(int employeeNo) {
		 return sqlSession.selectList("mapper.course.selectTeachingListByEmployee", employeeNo);
	}

}
