package com.kh.khedu.vo.exam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import lombok.Data;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ExamSearchVO extends PaginationVO {
    //검색 조건
    private Integer courseNo;
    private String examTitle;
    private String examStatus;
    //서버 내부용 - 강사 본인 시험만 조회할 때 사용
    private Integer employeeNo;
}