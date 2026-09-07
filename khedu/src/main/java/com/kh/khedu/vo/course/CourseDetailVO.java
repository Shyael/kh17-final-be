package com.kh.khedu.vo.course;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강의 상세조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseDetailVO {
	private int courseNo;
	
    private int employeeNo;
    private String employeeName; //accountName
    
    private int gradeNo;
    private int gradeLevel; //grade 테이블 grade명 초1 초2 초3 ...
    
    private String courseTitle;
    private String courseSubject;
    
    private int courseLimit;
    private int courseCurrentCount;
    
    private int courseFee;
    
    private String courseStatus;
    private String courseInfo;
    
    private Timestamp courseCreated;
    private Timestamp courseEdited;
    
    private String courseType;
}
