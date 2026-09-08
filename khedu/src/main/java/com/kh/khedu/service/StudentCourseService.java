package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dao.StudentCourseDao;
import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.dto.StudentCourseDto;
import com.kh.khedu.vo.studentCourse.StudentCourseVO;

@Service
public class StudentCourseService {
    
    @Autowired
    private StudentCourseDao studentCourseDao;
    @Autowired
    private StudentDao studentDao; // 학생 정보(학년)를 가져오기 위함
    @Autowired
    private CourseDao courseDao;   // 강의 정보(대상 학년)를 가져오기 위함
    
 // 수강 신청 메서드 리팩토링 (에러 메시지를 반환하도록 String 타입으로 변경)
    public String enrollCourse(StudentCourseDto studentCourseDto) {
        
        // --- [검증 1] 학년 일치 여부 체크 ---
        // (미리 만들어둔 조회 메서드를 활용한다고 가정)
        String studentGrade = studentDao.selectStudentGrade(studentCourseDto.getStudentNo()); 
        String courseGrade = courseDao.selectCourseGrade(studentCourseDto.getCourseNo());
        
        // 학년이 다르면 빠꾸!
        if (!studentGrade.equals(courseGrade)) {
            return "GRADE_MISMATCH"; // "해당 학년 전용 강의가 아닙니다."
        }

        // --- [검증 2] 시간표 겹침 체크 ---
        // 위에서 만든 MyBatis 쿼리를 실행합니다.
        int conflictCount = studentCourseDao.checkEnrollmentValidation(studentCourseDto);
        
        // 겹치는 스케줄이 1개라도 있다면 빠꾸!
        if (conflictCount > 0) {
            return "TIME_CONFLICT"; // "기존 수강 중인 강의와 시간이 겹칩니다."
        }

        // --- 모든 검증을 통과했다면 안전하게 DB Insert ---
        studentCourseDao.insert(studentCourseDto);
        return "SUCCESS";
    }

    
    public boolean update(StudentCourseDto studentCourseDto) {
    	return studentCourseDao.update(studentCourseDto);
    }

    // 학생별 수강 목록 조회
    public List<StudentCourseVO> getListByStudentNo(int studentNo) {
        return studentCourseDao.selectListByStudentNo(studentNo);
    }
}