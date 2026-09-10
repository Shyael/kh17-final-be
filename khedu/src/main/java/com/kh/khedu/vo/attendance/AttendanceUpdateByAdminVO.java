package com.kh.khedu.vo.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "관리자의 학생 출결관리 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceUpdateByAdminVO {
	private int attendanceNo;
	private String attendanceState;
}
