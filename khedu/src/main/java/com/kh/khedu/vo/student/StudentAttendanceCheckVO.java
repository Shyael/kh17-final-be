package com.kh.khedu.vo.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class StudentAttendanceCheckVO {
    private int studentNo;       // 학생 시퀀스 번호
    private String studentPhone; // 학생 휴대폰 번호
}