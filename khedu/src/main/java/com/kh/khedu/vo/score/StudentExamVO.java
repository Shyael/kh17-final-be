package com.kh.khedu.vo.score;

import lombok.Data;

@Data
public class StudentExamVO {
    private String scoreName; // ex: "1학기 중간고사"
    private String scoreType; // ex: "내신" 또는 "모의고사"
}
