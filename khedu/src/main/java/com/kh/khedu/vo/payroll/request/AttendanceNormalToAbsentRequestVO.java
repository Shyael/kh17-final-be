package com.kh.khedu.vo.payroll.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class AttendanceNormalToAbsentRequestVO {
	   @Positive
	   @NotNull
	    private Long empAttendanceNo;

	    @NotBlank
	    private String attendanceType;
}
