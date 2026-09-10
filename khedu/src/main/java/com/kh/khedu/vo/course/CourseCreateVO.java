package com.kh.khedu.vo.course;

import java.util.List;

import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강좌 등록 화면 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseCreateVO {
	private List<EmployeeSearchByNameVO> employees;
}
