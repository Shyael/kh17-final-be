package com.kh.khedu.vo.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor  @AllArgsConstructor
public class StudentCourseListVO {
	private int courseNo;
    private String courseTitle;
}
