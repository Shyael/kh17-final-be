package com.kh.khedu.vo.jwt;


import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TokenParseResponseVO {
	private int accountNo;
	private String accountId;	
	private String accountType; // 직원, 학생, 학부모
	private List<String> roleNames;//권한 
	private int noType; // 직원이면 직원번호, 학생이면 학생번호, 학부모면 학부모 번호

}
