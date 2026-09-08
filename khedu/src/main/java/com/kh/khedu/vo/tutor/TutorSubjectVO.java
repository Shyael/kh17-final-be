package com.kh.khedu.vo.tutor;

import java.util.List;

import com.kh.khedu.dto.AcademySubjectDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "강의 등록시 강사 조회 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorSubjectVO {
	private int tutorNo;
	private int employeeNo;
	private String accountName;
	private String accountPhone;
	private int academySubjectNo;
	
	private List<AcademySubjectDto> subjectList;
}
