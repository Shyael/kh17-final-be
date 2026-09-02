	package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WorkScheduleAddRequestVO {

	@NotNull
	@Positive
    private Long contractNo;

    private Timestamp scheduledWorkDate;

    private Timestamp scheduledClockIn;

    private Timestamp scheduledClockOut;

    private String scheduledDayType;
    
    @NotNull
    @Positive
    private Long workScheduleNo;
}