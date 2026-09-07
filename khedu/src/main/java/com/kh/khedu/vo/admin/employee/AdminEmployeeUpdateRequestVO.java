package com.kh.khedu.vo.admin.employee;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "관리자 직원 수정 요청 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class AdminEmployeeUpdateRequestVO {
	private String accountName;
    private String accountPhone;

    private String employeeType;
    private String employeeStatus;

    private List<Integer> roleNos;
}
