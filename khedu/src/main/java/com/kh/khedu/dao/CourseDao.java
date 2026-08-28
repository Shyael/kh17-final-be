package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.CourseDto;

public interface CourseDao {
	List<CourseDto> selectTeachingListByEmployee(int employeeNo);
}
