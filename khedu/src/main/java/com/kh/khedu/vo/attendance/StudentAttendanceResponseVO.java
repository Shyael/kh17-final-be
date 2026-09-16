package com.kh.khedu.vo.attendance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "학생 - 강좌별 출석 정보 전체 목록 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentAttendanceResponseVO {
	private Integer courseNo;
    private String courseTitle;
    private Integer tutorNo;
    private String tutorName;
    private String studentCourseStatus; // '수강중','수강완료','취소','중도퇴원'
    private StudentAttendanceSummaryVO summary;
    private List<StudentAttendanceItemVO> attendanceList; // 일자별 상세 출근
}
