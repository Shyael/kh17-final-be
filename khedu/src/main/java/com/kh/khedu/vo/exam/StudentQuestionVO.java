package com.kh.khedu.vo.exam;

import java.util.List;

import com.kh.khedu.dto.AttachDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생용 시험 문제 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentQuestionVO {
    private int questionNo;
    private int examNo;
    private String questionContent;
    private int questionScore;
    private int questionOrder;
    // 학생에게 보여줄 보기
    private List<StudentQuestionOptionVO> optionList;
    // 문제 이미지/첨부파일
    private List<AttachDto> fileList;
}