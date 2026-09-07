package com.kh.khedu.vo.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "본인 정보 변경용 VO")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChangeEmployeeRequestVO {
	private String accountName;
	private String accountPhone;
	private String accountBirth;
}
