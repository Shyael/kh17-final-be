package com.kh.khedu.vo.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강의 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseListVO {
	private Integer courseNo;
    private String courseTitle;
    private String courseSubject;
    private String gradeLevel; //grade 테이블 grade명 초1 초2 초3 ...    
    private String tutorName; //accountName
    
    private int courseLimit;
    private int courseCurrentCount;
    
    private String scheduleInfo;
    private String classroomInfo;
    
    private String courseStatus;
}
