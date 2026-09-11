package com.kh.khedu.vo.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="직원 이름 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeSearchByNameVO {

    private int employeeNo;
    private String accountName;
    private String accountId;
    private int accountNo;
}
