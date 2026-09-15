package com.kh.khedu.vo.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseStudentListVO {
	private int studentNo;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private String studentStatus; // 수강상태 (예: 수강중, 수료, 취소)
}
