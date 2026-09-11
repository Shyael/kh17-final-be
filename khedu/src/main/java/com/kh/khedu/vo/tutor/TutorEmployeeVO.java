package com.kh.khedu.vo.tutor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "강사 등록 가능 직원 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TutorEmployeeVO {

	private int employeeNo;
	private String accountName;

}