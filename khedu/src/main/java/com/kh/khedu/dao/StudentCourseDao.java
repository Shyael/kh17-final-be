package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.StudentCourseDto;
import com.kh.khedu.vo.studentCourse.StudentCourseVO;

public interface StudentCourseDao {
	void insert(StudentCourseDto studentCourseDto);
	boolean update(StudentCourseDto studentCourseDto);
    List<StudentCourseVO> selectListByStudentNo(int studentNo);
    int checkEnrollmentValidation(StudentCourseDto studentCourseDto);
}
