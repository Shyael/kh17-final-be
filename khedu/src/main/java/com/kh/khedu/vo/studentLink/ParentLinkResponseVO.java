package com.kh.khedu.vo.studentLink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생 연동 응답")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentLinkResponseVO {
	private int studentNo;
	private String studentName;
	private String relationship;
}
