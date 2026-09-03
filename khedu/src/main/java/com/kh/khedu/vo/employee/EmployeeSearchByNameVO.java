package com.kh.khedu.vo.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeSearchByNameVO {

    private int employeeNo;
    private String accountName;
    private String accountId;
    private int accountNo;
}
