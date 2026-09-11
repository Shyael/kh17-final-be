package com.kh.khedu.vo.payroll.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleSearchResponseVO {

    private List<WorkScheduleResponseVO> scheduleList;

    private WorkScheduleMonthlySummaryVO summary;
}