package com.kh.khedu.vo.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "강의 등록 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCreateRequestVO {
	@Schema(description = "0.강좌 번호", example="시퀀스 생성해서 넣어줌")
	private int courseNo;
	@Schema(description = "1.담당 강사 번호")
    private int employeeNo;
	@Schema(description = "2.학년 번호")
    private int gradeNo;
	@Schema(description = "3.강좌명")
    private String courseTitle;
	@Schema(description = "4.과목")
    private String courseSubject;
	@Schema(description = "5.수강 정원")
    private int courseLimit;
	@Schema(description = "6.수강료")
    private int courseFee;
	@Schema(description = "7.강좌 설명")
    private String courseInfo;
	@Schema(description = "8.강좌 유형", example="정규")
    private String courseType;
}
