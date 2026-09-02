package com.kh.khedu.vo.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class StudentCourseVO {
	private int studentNo;
	private int courseNo;
	private int courseFee;
}
