package com.kh.khedu.vo.student;

import lombok.Data;

@Data
public class StudentListResponseVO {
    //식별자
    private int studentNo;
    
    //Account 테이블 정보 (JOIN 필요)
    private String studentName;
    
    //Student 테이블 정보
    private String studentSchool;
    private String studentGrade;
    
    //통계 및 집계 데이터 (서브쿼리나 Service 단에서 계산)
    private int attendanceRate;
    private int unpaidAmount;
    private String riskLevel;
}