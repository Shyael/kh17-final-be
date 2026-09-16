package com.kh.khedu.vo.classSession;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WeeklyClassSessionVO {

    private int sessionNo;
    private int scheduleNo;

    // 실제 수업이 진행되는 강의실
    private Integer classroomNo;
    private String classroomName;

    private Timestamp sessionStart;
    private Timestamp sessionEnd;

    private String sessionStatus;
}