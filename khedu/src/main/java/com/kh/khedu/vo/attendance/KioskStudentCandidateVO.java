package com.kh.khedu.vo.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KioskStudentCandidateVO {
	private int studentNo;
	private String studentName;
	private String studentPhone;
}
