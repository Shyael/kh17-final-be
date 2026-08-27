package com.kh.khedu.vo.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TokenParseResponseVO {
    private int accountNo;
    private String accountId;    
    private String accountType; // 직원, 학생, 학부모
    private int roleNo; //권한 
    
}
