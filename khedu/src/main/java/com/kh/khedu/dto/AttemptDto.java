package com.kh.khedu.dto;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 응시 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttemptDto {
    private int attemptNo;
    private int examNo;
    private int studentNo;
    private String attemptStatus;
    // 시험 응시 시작 시간
    private Timestamp attemptStart;
    // 시험 제출 시간
    // 제출 전이면 null
    private Timestamp attemptSubmit;
    // 시험 점수
    // 채점 전이면 null
    private Integer attemptScore;
}