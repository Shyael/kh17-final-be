package com.kh.khedu.vo.attendance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "키오스크 학생 출결 체크 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class KioskAttendanceRequestVO {
	// 키패드로 입력한 휴대폰 뒷 4자리 (최초 입력 시 필수)
    private String phoneTail;
    // 뒷자리 중복 시 화면 모달에서 학생이 자기 이름을 눌렀을 때 넘어오는 고유 PK (선택값)
    private Integer selectedStudentNo;
 }
