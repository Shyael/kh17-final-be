package com.kh.khedu.vo.exam;

import java.util.List;

import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.dto.QuestionOptionDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사용 시험 문제 상세 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionManageDetailVO {
    private int questionNo;
    private int examNo;
    private String questionContent;
    private int questionScore;
    // 문제 해설
    private String questionComment;
    private int questionOrder;
    // 보기 목록
    // 강사용이므로 optionIsAnswer 포함
    private List<QuestionOptionDto> optionList;
    // 문제 첨부파일
    private List<AttachDto> fileList;
}