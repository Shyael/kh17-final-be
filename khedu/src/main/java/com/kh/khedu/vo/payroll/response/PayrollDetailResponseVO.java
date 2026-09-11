package com.kh.khedu.vo.payroll.response;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDetailResponseVO {

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

	// 공제 상세
	private List<PayrollDeductionResponseVO> deductionList;

	// 지급 / 취소 전체 이력
	private List<PayrollPaymentResponseVO> paymentList;

	// 현재 실제 지급되어 있는 금액
	// paid 총액 - cancelled 총액
	private Long currentPaidAmount;
}
