package com.kh.khedu.vo.assignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import lombok.Data;
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class AssignmentStudentSearchVO extends PaginationVO {
	//특정 강의
    private Integer courseNo;
    //과제 제목
    private String assignmentTitle;
    //과제 상태
    private String assignmentStatus;
    //로그인 학생 번호
    //Service에서 세팅
    private Integer studentNo;
    //과제 제출 상태
    private String submitStatus;
}
