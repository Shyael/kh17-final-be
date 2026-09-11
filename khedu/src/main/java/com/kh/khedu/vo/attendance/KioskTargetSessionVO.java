package com.kh.khedu.vo.attendance;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder@NoArgsConstructor@AllArgsConstructor
public class KioskTargetSessionVO {
    private int attendanceNo;
    private String attendanceState;
    private int sessionNo;
    private LocalDateTime sessionStart; // MyBatis가 자동 매핑해줌
    private LocalDateTime sessionEnd;
    private String courseTitle;
    private String studentName;
}