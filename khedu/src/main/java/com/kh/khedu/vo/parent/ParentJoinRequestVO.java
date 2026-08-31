package com.kh.khedu.vo.parent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학부모 회원가입(자녀 연결x)")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentJoinRequestVO {
	private String accountId;
	private String accountPassword;
	private String accountName;
	private String accountPhone;
	private String accountBirth;
}
