package com.kh.khedu.vo.score;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ScoreListResponseVO {
    private String scoreSubject; // 과목명 (국어, 수학 등)
    private int scoreScore;      // 원점수
    private Integer scoreRank;   // 등급 (ERD 기준 NULL 허용이므로 Integer 사용)
    private Integer rankDiff;    // 이전 시험 대비 등급 차이 (첫 시험이거나 이전 데이터가 없으면 NULL)
    private String scoreName;
    private String scoreType;
    private LocalDate scoreDate;
}