package com.kh.khedu.vo.attendance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "키오스크 학생 출결 체크 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class KioskAttendanceCheckRequestVO {
	private int studentNo;
	
	private String studentPhone;
}
