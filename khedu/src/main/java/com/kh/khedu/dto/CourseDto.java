package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강의정보 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseDto {
	public static final String STATUS_RECRUITING = "모집중";
	public static final String STATUS_RUNNING = "진행중";
	public static final String STATUS_CLOSED = "종강";
	
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
    @Schema(description = "6.현재 수강 신청한 인원")
    private int courseCurrentCount; //기본값 0
    @Schema(description = "7.수강료")
    private int courseFee;
    @Schema(description = "8.강좌 상태", example="모집중, 모집마감, 진행중, 종료, 페강")
    private String courseStatus; //기본값 모집중
    @Schema(description = "9.강좌 설명")
    private String courseInfo;
    @Schema(description = "10.강좌 생성일")
    private Timestamp courseCreated;
    @Schema(description = "11.강좌 수정일")
    private Timestamp courseEdited;
    @Schema(description = "12.강좌 유형", example="정규, 보충, 특강")
    private String courseType;
}