package com.kh.khedu.vo.parent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder@NoArgsConstructor@AllArgsConstructor
public class ParentUpdateRequestVO {
    private int parentNo;
    private int accountNo;
    private String accountName;
    private String accountPhone;
    private String accountBirth;
    private String accountStatus; // 'Y' 또는 'N'
}