package com.kh.khedu.vo.schedule;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WeeklyScheduleVO {
    private int scheduleNo;

    private int courseNo;
    private String courseTitle;

    private int employeeNo;
    private String employeeName;

    private int classroomNo;
    private String classroomName;

    private String scheduleWeek;
    private String scheduleStart;
    private String scheduleEnd;

    private LocalDate scheduleOpen;
    private LocalDate scheduleClose;
}