package com.kh.khedu.vo.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "강의등록 응답 vo")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseCreateResponseVO {
	private int courseNo;
}
