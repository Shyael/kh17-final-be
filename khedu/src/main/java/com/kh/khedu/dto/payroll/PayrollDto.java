package com.kh.khedu.dto.payroll;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDto {

	private Long payrollNo;
	private Integer employeeNo;

	private Integer payrollYear;
	private Integer payrollMonth;

	private Double totalWorkHours;
	private Double totalOvertimeHours;
	private Double totalNightHours;
	private Double totalHolidayHours;

	private Long basePay;
	private Long weekHolidayPay;
	private Long overtimePay;
	private Long nightPay;
	private Long holidayPay;

	private Long grossPay;
	private Long totalDeduction;
	private Long netPay;

	private String payrollStatus;

	private Timestamp calculatedAt;
	private Timestamp confirmedAt;

}