package com.kh.khedu.vo.tutor;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.TutorCareerDto;
import com.kh.khedu.dto.TutorSubjectDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사 상세 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorDetailVO {

	private int tutorNo;
	private int employeeNo;

	private String accountName;
	private String accountPhone;

	private String tutorTagline;
	private String tutorIntro;

	private Timestamp tutorWtime;
	private Timestamp tutorEtime;

	private List<TutorSubjectDto> subjectList;
	private List<TutorCareerDto> careerList;

}