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
    private int courseNo;
    private int employeeNo;
    private int gradeNo;
    private String courseTitle;
    private String courseSubject;
    private int courseLimit;
    private int courseCurrentCount;
    private int courseFee;
    private String courseStatus;
    private String courseInfo;
    private Timestamp courseCreated;
    private Timestamp courseEdited;
    private String courseType;
}