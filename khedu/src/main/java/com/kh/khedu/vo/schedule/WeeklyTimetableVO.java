package com.kh.khedu.vo.schedule;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WeeklyTimetableVO {
    // 반복 스케줄
    private int scheduleNo;

    // 실제 수업 - 아직 생성 전이면 null
    private Integer sessionNo;

    // 강의
    private int courseNo;
    private String courseTitle;

    // 강사
    private int employeeNo;
    private String employeeName;

    // 실제 표시할 강의실
    private Integer classroomNo;
    private String classroomName;

    // 실제 표시 날짜/시간
    private LocalDate classDate;
    private LocalTime startTime;
    private LocalTime endTime;

    // 예정 / 진행중 / 종료 / 취소
    private String classStatus;
}