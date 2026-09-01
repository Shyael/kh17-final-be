package com.kh.khedu.vo.studentLink;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학생 연동코드 발송 응답")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentLinkResponseVO {
	private String linkCode; //연동코드
	private Timestamp linkExpire; //만료시각
}
