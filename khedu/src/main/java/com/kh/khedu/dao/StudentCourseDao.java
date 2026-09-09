package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.StudentCourseDto;
import com.kh.khedu.vo.studentCourse.AvailableCourseVO;
import com.kh.khedu.vo.studentCourse.StudentCourseVO;

public interface StudentCourseDao {
	void insert(StudentCourseDto studentCourseDto);
	boolean update(StudentCourseDto studentCourseDto);
    List<StudentCourseVO> selectListByStudentNo(int studentNo);
    String checkEnrollmentValidation(StudentCourseDto studentCourseDto);
    // 모집중인 전체 강의 목록 조회
    List<AvailableCourseVO> selectAllCourse();
}
