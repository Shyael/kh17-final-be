package com.kh.khedu.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.khedu.service.ParentService;
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.parent.ParentJoinRequestVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test04_학부모50명대량등록테스트2 {

    @Autowired
    private ParentService parentService;

    @Test
    public void register50Parents() {

        int totalCount = 50;

        for (int i = 1; i <= totalCount; i++) {

            String formattedIndex = String.format("%03d", i);

            String accountId = "parent" + formattedIndex + "@khedu.com";

            // 010 + 4자리 + 4자리
            String accountPhone = "010" 
                    + (3000 + i) 
                    + String.format("%04d", i);

            String accountName = "학부모" + formattedIndex;

            ParentJoinRequestVO requestVO = ParentJoinRequestVO.builder()
                    .accountId(accountId)
                    .accountPassword("TestPass123!")
                    .accountName(accountName)
                    .accountPhone(accountPhone)
                    .accountBirth("1975-05-15")
                    .build();

            try {

                AccountJoinResponseVO response =
                        parentService.joinParent(requestVO);

                log.info(
                        "[{}/{}] 등록 완료 - ID: {}, 이름: {}, 번호: {}, 학부모번호: {}",
                        i,
                        totalCount,
                        response.getAccountId(),
                        response.getAccountName(),
                        accountPhone,
                        response.getTargetNo()
                );

            } catch (Exception e) {

                log.error(
                        "[{}/{}] 등록 실패: {} - 사유: {}",
                        i,
                        totalCount,
                        accountId,
                        e.getMessage(),
                        e
                );
            }
        }
    }
}