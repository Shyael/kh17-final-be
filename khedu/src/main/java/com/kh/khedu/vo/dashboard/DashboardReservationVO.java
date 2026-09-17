package com.kh.khedu.vo.dashboard;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardReservationVO {
    private int reservationNo;
    private String reservationName;
    private String reservationPhone;
    private String reservationType;

    // 상담 예정 시간
    private Timestamp reservationTime;

    // 0 대기 / 1 확정 / 2 완료 / 9 취소
    private String reservationStatus;

    // 상담 신청 시간
    private Timestamp reservationCtime;
}