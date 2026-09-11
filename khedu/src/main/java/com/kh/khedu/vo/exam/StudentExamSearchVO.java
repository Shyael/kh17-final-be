package com.kh.khedu.vo.exam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import lombok.Data;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class StudentExamSearchVO extends PaginationVO {
    //검색 조건
    private Integer courseNo;
    private String examTitle;
    //응시상태
    private String attemptStatus;
    private String examPhase;
    //서버 내부용
    private Integer studentNo;
}