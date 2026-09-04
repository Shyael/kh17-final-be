package com.kh.khedu.vo.exam;

import java.sql.Timestamp;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "시험 상세 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExamDetailVO {
	private int examNo;
	private int courseNo;
	//강의명
	private String courseTitle;
	private int employeeNo;
	//출제 강사명
	private String accountName;
	private String examTitle;
	private String examInfo;
	private Timestamp examStart;
	private Timestamp examEnd;
	//null이면 시간 제한 없음
	private Integer examLimit;
	private String examStatus;
	private Timestamp examWtime;
	//문제목록
	private List<QuestionManageDetailVO> questionList;
}
