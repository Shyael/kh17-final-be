package com.kh.khedu.vo.studentCourse; // 패키지명은 프로젝트에 맞게 수정해주세요!

import lombok.Data;

@Data
public class StudentCourseVO {
    // 1. student_course 테이블 (내 테이블)
    private int courseNo;
    private int studentNo;
    private String studentCourseStatus; // 신청, 수강중, 취소 등

    // 2. course 테이블에서 JOIN으로 가져올 데이터 (팀원 테이블)
    private String courseTitle;   // 강의명
    private String courseSubject; // 과목 (예: 국어, 수학)
    private String courseType;    // 강의유형 (정규, 특강 등)
    private String courseStatus;  // 강의상태 (진행중, 종강 등)
}