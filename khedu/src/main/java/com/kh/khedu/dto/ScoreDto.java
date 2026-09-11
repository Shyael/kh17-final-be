package com.kh.khedu.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ScoreDto {
	private int scoreNo;
	private int studentNo;
	private String scoreType;
	private String scoreName;
	private String scoreSubject;
	private int scoreScore;
	private int scoreRank;
	private LocalDate scoreDate;
	private Timestamp scoreWtime;
}
