package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceFindVO {
	 private Long empAttendanceNo;

	    private Long contractNo;

	    private int employeeNo;

	    private Timestamp workDate;

	    private Boolean working;
}
