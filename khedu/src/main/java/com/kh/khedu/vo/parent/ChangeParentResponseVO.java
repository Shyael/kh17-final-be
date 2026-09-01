package com.kh.khedu.vo.parent;

import java.sql.Timestamp;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name= "학부모 개인정보 수정 응답")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChangeParentResponseVO {
	private String accountName;
	private String accountPhone;
	private String accountBirth;
	private Timestamp accountUtime;
}
