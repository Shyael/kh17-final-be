package com.kh.khedu.vo.assignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import lombok.Data;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class AssignmentSearchVO extends PaginationVO {
    //특정 강의 과제 조회
    private Integer courseNo;
    //과제 제목 검색
    private String assignmentTitle;
    //과제 상태 검색
    private String assignmentStatus;
    //로그인한 강사 번호
    //프론트에서 받는 값이 아니라 Service에서 세팅
    private Integer employeeNo;
}