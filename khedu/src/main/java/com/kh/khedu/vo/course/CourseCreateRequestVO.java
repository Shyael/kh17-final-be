package com.kh.khedu.vo.course;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(name = "강의 등록 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCreateRequestVO {
	@Schema(description = "0.담당 강사 번호")
	@Positive(message = "강사를 선택해주세요.")
    private int employeeNo;
	
	@Schema(description = "1.강사 과목 번호")
	@Positive(message = "과목을 선택해주세요.")
    private int academySubjectNo; //해당 강사가 선택한 과목을 담당하는지 검증하기 위한 값
	
	@Schema(description = "2.학년 번호")
	@Positive(message = "학년을 선택해주세요.")
    private int gradeNo;
	
	@Schema(description = "3.강좌명")
	@NotBlank(message = "강좌명을 입력해주세요.")
    private String courseTitle;
	
	@Schema(description = "4.과목")
	@NotBlank(message = "과목을 입력해주세요.")
    private String courseSubject;
	
	@Schema(description = "5.수강 정원")
	@Min(value = 1, message = "정원은 1명 이상이어야 합니다.")
    private int courseLimit;
	
	@Schema(description = "6.수강료")
	@Min(value = 0, message = "수강료는 0원 이상이어야 합니다.")
    private int courseFee;
	
	@Schema(description = "7.강좌 설명")
    private String courseInfo;
	
	@Schema(description = "8.강좌 유형", example="정규")
	@NotBlank(message = "강좌 유형을 선택해주세요.")
    private String courseType;
	
	@NotEmpty(message = "수업 일정을 최소 1개 이상 등록해주세요.")
	@Valid // 내부 리스트 객체들까지 재귀적으로 검증
	private List<ScheduleCreateRequestVO> schedules;
}
