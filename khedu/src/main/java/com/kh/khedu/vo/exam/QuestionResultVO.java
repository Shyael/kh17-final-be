package com.kh.khedu.vo.exam;

import java.util.List;

import com.kh.khedu.dto.AttachDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 문제 결과 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionResultVO {
    private int questionNo;
    private String questionContent;
    private int questionScore;
    private int questionOrder;
    // 해설
    private String questionComment;
    // Y / N / null(미응답)
    private String isCorrect;
    // 보기 목록
    private List<QuestionResultOptionVO> optionList;
    // 문제 첨부파일
    private List<AttachDto> fileList;
}