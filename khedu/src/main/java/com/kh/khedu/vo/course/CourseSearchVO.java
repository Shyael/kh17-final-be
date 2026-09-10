package com.kh.khedu.vo.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name="강의 검색용 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class CourseSearchVO extends PaginationVO {
	private String courseTitle;
	private String courseSubject;
	private Integer gradeNo;
	private String courseStatus;
}
