package com.kh.khedu.vo.course;

import java.util.List;

import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강의 등록 가능한 부분 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseVO {
	@Schema(description = "0.강의 번호")
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
	
	private List<ScheduleCreateRequestVO> schedules;
}
