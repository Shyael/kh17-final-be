package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentCourseDto;
import com.kh.khedu.vo.studentCourse.AvailableCourseVO;
import com.kh.khedu.vo.studentCourse.StudentCourseVO;

@Repository
public class StudentCourseDaoMybatis implements StudentCourseDao {
    
    @Autowired
    private SqlSession sqlSession;
    
    @Override
    public void insert(StudentCourseDto studentCourseDto) {
    	sqlSession.insert("mapper.studentCourse.insert", studentCourseDto);
    }
    
    @Override
    public boolean update(StudentCourseDto studentCourseDto) {
    	return sqlSession.update("mapper.studentCourse.update", studentCourseDto)>0;
    }

    @Override
    public List<StudentCourseVO> selectListByStudentNo(int studentNo) {
        return sqlSession.selectList("mapper.studentCourse.selectListByStudentNo", studentNo);
    }
    
    @Override
    public String checkEnrollmentValidation(StudentCourseDto studentCourseDto) {
    	return sqlSession.selectOne("mapper.studentCourse.checkEnrollmentValidation", studentCourseDto);
    }
    
    @Override
    public List<AvailableCourseVO> selectAllCourse() {
        return sqlSession.selectList("mapper.studentCourse.selectAllCourse");
    }
}
