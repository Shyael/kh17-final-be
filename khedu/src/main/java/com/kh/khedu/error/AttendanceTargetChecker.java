package com.kh.khedu.error;

import org.springframework.stereotype.Component;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Component
public class AttendanceTargetChecker {

    public void check(
            TokenParseResponseVO parseVO
    ) {

        if (parseVO.getRoleNames().contains("ADMIN")) {
            throw new GetOutException();
        }
    }
}