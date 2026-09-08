package com.kh.khedu.vo.classroom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "강의실 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClassroomWhenRegisterVO {
	private int classroomNo;
    private String classroomName;
    private int classroomCapacity;
    private String classroomStatus;
}
