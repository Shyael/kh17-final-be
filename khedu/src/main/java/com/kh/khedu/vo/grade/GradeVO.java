package com.kh.khedu.vo.grade;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "학년 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeVO {
	 private int gradeNo;
	 private String gradeLevel;
}
