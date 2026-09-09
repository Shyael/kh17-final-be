package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dao.StudentCourseDao;
import com.kh.khedu.dao.StudentDao;
import com.kh.khedu.dto.StudentCourseDto;
import com.kh.khedu.vo.studentCourse.AvailableCourseVO;
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
        
        // 1. 검증 쿼리 한 방에 호출 (학년 일치 & 시간표 겹침 동시 검사)
        String validationResult = studentCourseDao.checkEnrollmentValidation(studentCourseDto);
        
        // 2. 결과가 "OK"가 아니라면 튕겨내기
        if (!"OK".equals(validationResult)) {
            return validationResult; 
        }

        // 3. 모든 검증을 무사히 통과했다면 수강 내역
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
    
    // 모달창에 띄울 모집중인 강의 목록 가져오기
    public List<AvailableCourseVO> getAvailableCourseList() {
        return studentCourseDao.selectAllCourse();
    }
}