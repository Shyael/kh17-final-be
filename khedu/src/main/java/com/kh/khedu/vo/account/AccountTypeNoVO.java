package com.kh.khedu.vo.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(name="계정번호를 통해 유형번호 찾은 객체")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeNoVO {
	private Integer accountNo;
    private String accountType;

    private Integer employeeNo;
    private Integer studentNo;
    private Integer parentNo;
}
