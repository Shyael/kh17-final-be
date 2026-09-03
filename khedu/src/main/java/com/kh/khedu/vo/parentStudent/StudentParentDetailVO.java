package com.kh.khedu.vo.parentStudent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학생의 학부모 정보")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentParentDetailVO {
	private int parentNo;
	private String parentName;
	private String relationship;
}
