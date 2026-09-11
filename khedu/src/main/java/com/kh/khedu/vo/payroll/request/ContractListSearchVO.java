package com.kh.khedu.vo.payroll.request;
import java.time.LocalDate;

import com.kh.khedu.util.PaginationVO;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ContractListSearchVO
        extends PaginationVO {

    // 직원 이름
    private String accountName;

    // 계약 시작일
    
    private LocalDate contractStart;

    // 계약 종료일
    
    private LocalDate contractEnd;

    // 직원 상태
    private String employeeStatus;
}