package com.kh.khedu.vo.studentLink;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="연동링크 저장할 정보")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentLinkVO {
	private int studentLinkNo;
	private int studentNo;
	private String linkCode;
	private Timestamp expire;
}
