package com.kh.khedu.vo.dashboard;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardExamVO {

    private int examNo;
    private int courseNo;

    private String courseTitle;
    private String examTitle;

    private String examPhase;

    private Timestamp examStart;
    private Timestamp examEnd;
}