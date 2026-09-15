package com.kh.khedu.vo.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Schema(name="학생 수강중인 강좌 목록 조회(화면 상단 셀렉트용) VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseSelectBarVO {
	private Integer courseNo;
	private String courseTitle;
	private String courseStatus; //모집중/모집마감/진행중/종료/폐강
}
