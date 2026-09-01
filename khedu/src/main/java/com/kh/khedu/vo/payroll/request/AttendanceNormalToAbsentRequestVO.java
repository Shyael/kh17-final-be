package com.kh.khedu.vo.payroll.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class AttendanceNormalToAbsentRequestVO {
	   @Positive
	    private long empAttendanceNo;

	    @NotBlank
	    private String attendanceType;
}
