package com.kh.khedu.vo.parent;

import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학부모 개인정보 조회용")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentDetailVO {
	// parent
	private int parentNo;
	// parent_student
	private Integer studentNo;
	private String relationship; // 부 모 보호자 기타
	// account
    private Timestamp accountUtime;
    private int accountNo;
    private String accountId;
    private String accountBirth;
    private String accountName;
    private String accountPhone;
    private String accountStatus;
    private String accountType;
}
