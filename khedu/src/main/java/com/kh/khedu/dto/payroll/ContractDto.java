package com.kh.khedu.dto.payroll;

import java.sql.Timestamp;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="근로계약 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContractDto {
	// 근로계약 번호
    private long contractNo;

    // 직원 번호
    private int employeeNo;

    // 급여 형태
    // monthly / hourly / daily
    private String wageType;

    // 기준 임금
    private long baseWage;

    // 1일 소정근로시간
    private double dailyWorkHours;

    // 주 소정근로시간
    private double weeklyWorkHours;

    // 계약 시작일
    private Timestamp contractStart;

    // 계약 종료일
    private Timestamp contractEnd;

    // 매월 급여 지급 예정일
    private int payday;

    // 계약서 본문
    private String contractContent;

    // 직원 서명
    private String employeeSignature;

    // 원장 서명
    private String employerSignature;

    // 계약 체결(서명) 시각
    private Timestamp signedTime;

    // 계약 상태
    // scheduled / active / ended
    private String contractStatus;
}
