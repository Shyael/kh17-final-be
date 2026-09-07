package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WorkScheduleUpdateRequestVO {
	   private long workScheduleNo;

	    private Timestamp scheduledWorkDate;
	    private Timestamp scheduledClockIn;
	    private Timestamp scheduledClockOut;
	    private String scheduledDayType;
	    
	    @NotNull
	    @PositiveOrZero
	    private Double actualWorkHours;
	    @NotNull
	    @PositiveOrZero
	    private Double actualOvertimeHours;
	    @NotNull
	    @PositiveOrZero
	    private Double actualNightHours;
	    @NotNull
	    @PositiveOrZero
	    private Double actualHolidayHours;
}
