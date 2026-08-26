package com.kh.khedu.vo.academy;

import java.util.List;

import com.kh.khedu.dto.AcademyDto;
import com.kh.khedu.dto.AcademyHistoryDto;
import com.kh.khedu.dto.AcademySubjectDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AcademyDetailResponseVO {
	private AcademyDto academy;
	private List<AcademyHistoryDto> historyList;
	private List<AcademySubjectDto> subjectList;
}
