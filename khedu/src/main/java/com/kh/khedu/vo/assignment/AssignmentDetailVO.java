package com.kh.khedu.vo.assignment;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.AttachDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "과제 상세정보 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssignmentDetailVO {
    private int assignmentNo;
    private int courseNo;
    private String courseTitle;//강좌이름
    private int employeeNo;
    private String accountName;//작성자이름
    private String assignmentTitle;//과제제목
    private String assignmentContent;//과제내용
    private String assignmentStatus;//과제상태
    private Timestamp assignmentDueDate;//과제마감일
    private Timestamp assignmentWtime;
    //과제 파일
    private List<AttachDto> fileList;
}