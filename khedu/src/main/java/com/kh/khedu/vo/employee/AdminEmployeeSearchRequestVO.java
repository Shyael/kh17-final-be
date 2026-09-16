package com.kh.khedu.vo.employee;

import com.kh.khedu.util.PaginationVO;

import lombok.Data;

@Data
public class AdminEmployeeSearchRequestVO extends PaginationVO{

    private String accountName;

    private String employeeType;

}