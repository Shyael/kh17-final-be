package com.kh.khedu.vo.parentStudent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학부모 자녀 정보")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentStudentDetailVO {
	private int studentNo;
	private String studentName;
	private String relationship;
}
