package com.kh.khedu.vo.dashboard;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardAssignmentVO {

    private int assignmentNo;
    private int courseNo;

    private String courseTitle;
    private String assignmentTitle;

    private String assignmentPhase;

    private Timestamp assignmentDueDate;
}