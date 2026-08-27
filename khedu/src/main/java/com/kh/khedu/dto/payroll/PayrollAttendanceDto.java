package com.kh.khedu.dto.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollAttendanceDto {
	 // 급여 반영 근태 번호
    private long payrollAttendanceNo;

    // 급여 번호
    private long payrollNo;

    // 직원 근태 번호
    private long empAttendanceNo;
}
