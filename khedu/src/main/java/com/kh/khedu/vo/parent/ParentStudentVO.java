package com.kh.khedu.vo.parent;

import com.kh.khedu.dto.ParentDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학부모학생 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentStudentVO {
	private Integer parentNo;
	private Integer accountNo;
	private Integer studentNo;  
	private String relationship; // 부 / 모 / 기타 / 보호자
}
