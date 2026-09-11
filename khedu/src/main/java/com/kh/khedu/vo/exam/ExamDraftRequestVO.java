package com.kh.khedu.vo.exam;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 임시저장 요청 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamDraftRequestVO {
	// 현재 화면에 존재하는 문제 목록
    private List<QuestionDraftVO> questionList;
    // 기존 DB 문제 중 화면에서 삭제된 문제번호
    private List<Integer> deletedQuestionNos;
    // 기존 DB 보기 중 화면에서 삭제된 보기번호
    private List<Integer> deletedOptionNos;
}