package com.kh.khedu.vo.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceSearchVO {
	private Integer courseNo;          // 강좌 번호 (COURSE_NO)
	private String courseTitle;
    private String studentName;     // 학생 이름
    private String startDate;       // 검색 시작일 (YYYY-MM-DD)
    private String endDate;         // 검색 종료일 (YYYY-MM-DD)
    private String attendanceState;// 출결 상태 (출석, 지각, 조퇴, 결석 등)
    private String sessionStatus;   // 세션 상태 ('진행중', '종료', '취소')
}
