package com.kh.khedu.dto.payroll;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PayrollDto {

    // 급여 번호
    private long payrollNo;

    // 근로계약 번호
    private long contractNo;

    // 급여 대상 연도
    private int payrollYear;

    // 급여 대상 월
    private int payrollMonth;

    // 급여 상태
    // calculating / confirmed / paid
    private String payrollStatus;
}
