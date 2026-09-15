package com.kh.khedu.vo.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name="강좌번호로 강좌정보 조회 VO")
@Data
public class CourseTutorVO {
	private Integer tutorNo;
	private String accountName;
}
